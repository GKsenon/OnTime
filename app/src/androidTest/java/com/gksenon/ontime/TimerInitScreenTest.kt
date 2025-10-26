package com.gksenon.ontime

import android.app.Activity
import android.view.Window
import android.view.WindowManager
import androidx.activity.compose.LocalActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTouchInput
import com.gksenon.ontime.domain.Preset
import com.gksenon.ontime.ui.TimerInitScreen
import com.gksenon.ontime.viewmodel.TimerInitViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class TimerInitScreenTest {

    @get:Rule
    val rule = createComposeRule()

    private val mockState = MutableStateFlow(TimerInitViewModel.State())
    private val viewModel = mockk<TimerInitViewModel> {
        every { onHoursChanged(any()) } returns Unit
        every { onMinutesChanged(any()) } returns Unit
        every { onSecondsChanged(any()) } returns Unit
        every { onPresetHoursChanged(any()) } returns Unit
        every { onPresetMinutesChanged(any()) } returns Unit
        every { onPresetSecondsChanged(any()) } returns Unit
        every { state } returns mockState
        every { onCreatePresetButtonClicked() } returns Unit
    }
    private val navigateToTimerInProgress: (Duration) -> Unit = mockk()
    private val navigateToZenModeSettings: () -> Unit = mockk()

    private lateinit var hoursContentDescription: String
    private lateinit var minutesContentDescription: String
    private lateinit var secondsContentDescription: String
    private lateinit var noPresetsText: String
    private lateinit var startButtonText: String
    private lateinit var createPresetText: String
    private lateinit var presetDurationContentDescription: String
    private lateinit var okButtonText: String
    private lateinit var cancelButtonText: String
    private lateinit var editPresetButtonText: String
    private lateinit var deletePresetButtonText: String
    private lateinit var windowManager: Window

    @Before
    fun before() {
        rule.setContent {
            TimerInitScreen(viewModel, navigateToTimerInProgress, navigateToZenModeSettings)
            hoursContentDescription = stringResource(R.string.hours_content_description)
            minutesContentDescription = stringResource(R.string.minutes_content_description)
            secondsContentDescription = stringResource(R.string.seconds_content_description)
            noPresetsText = stringResource(R.string.no_presets)
            startButtonText = stringResource(R.string.start)
            createPresetText = stringResource(R.string.create_preset)
            presetDurationContentDescription =
                stringResource(R.string.preset_duration_picker_content_description)
            okButtonText = stringResource(android.R.string.ok)
            cancelButtonText = stringResource(android.R.string.cancel)
            editPresetButtonText = stringResource(R.string.edit_preset)
            deletePresetButtonText = stringResource(R.string.delete_preset)
            windowManager = LocalActivity.current!!.window
        }
    }

    @Test
    fun init_showsInitState() {
        rule.onNodeWithContentDescription(hoursContentDescription)
            .onChildren()
            .filterToOne(hasText("00"))
            .assertIsDisplayed()
        rule.onNodeWithContentDescription(minutesContentDescription)
            .onChildren()
            .filterToOne(hasText("00"))
            .assertIsDisplayed()
        rule.onNodeWithContentDescription(secondsContentDescription)
            .onChildren()
            .filterToOne(hasText("00"))
            .assertIsDisplayed()
        rule.onNodeWithText(noPresetsText).assertIsDisplayed()
        assert((windowManager.attributes.flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) == 0)
    }

    @Test
    fun onHoursUpdated_sendsUpdateToVM() {
        val hoursSlot = slot<Int>()
        every { viewModel.onHoursChanged(capture(hoursSlot)) } returns Unit

        val hours = 1
        rule.onNodeWithContentDescription(hoursContentDescription)
            .performScrollToIndex(getScrollPosition(24, hours))

        assertEquals(hours, hoursSlot.captured)
    }

    @Test
    fun onMinutesUpdated_sendsUpdateToVM() {
        val minutesSlot = slot<Int>()
        every { viewModel.onMinutesChanged(capture(minutesSlot)) } returns Unit

        val minutes = 3
        rule.onNodeWithContentDescription(minutesContentDescription)
            .performScrollToIndex(getScrollPosition(60, minutes))

        assertEquals(minutes, minutesSlot.captured)
    }

    @Test
    fun onSecondsUpdated_sendsUpdateToVM() {
        val secondsSlot = slot<Int>()
        every { viewModel.onSecondsChanged(capture(secondsSlot)) } returns Unit

        val seconds = 5
        rule.onNodeWithContentDescription(secondsContentDescription)
            .performScrollToIndex(getScrollPosition(60, seconds))

        assertEquals(seconds, secondsSlot.captured)
    }

    @Test
    fun onDurationUpdated_showsNewDuration() {
        mockState.value = mockState.value.copy(duration = 1.hours + 30.minutes + 45.seconds)

        rule.onNodeWithContentDescription(hoursContentDescription)
            .onChildren()
            .filterToOne(hasText("01"))
            .assertIsDisplayed()
        rule.onNodeWithContentDescription(minutesContentDescription)
            .onChildren()
            .filterToOne(hasText("30"))
            .assertIsDisplayed()
        rule.onNodeWithContentDescription(secondsContentDescription)
            .onChildren()
            .filterToOne(hasText("45"))
            .assertIsDisplayed()
    }

    @Test
    fun onStartButtonClicked_startsTimer() {
        every { viewModel.onStartButtonClicked() } returns Unit
        every { navigateToTimerInProgress(any()) } returns Unit

        rule.onNodeWithText(startButtonText).performClick()
        verify { viewModel.onStartButtonClicked() }

        val duration = 30.minutes + 45.seconds
        mockState.value =
            mockState.value.copy(duration = duration, navigateToTimerInProgress = true)
        rule.mainClock.advanceTimeBy(1000L)

        verify { navigateToTimerInProgress(duration) }
    }

    @Test
    fun onCreatePresetButtonClicked_showsDialog() {
        rule.onNodeWithContentDescription(createPresetText).performClick()
        verify { viewModel.onCreatePresetButtonClicked() }

        mockState.value = mockState.value.copy(showCreatePresetDialog = true)
        rule.onNodeWithText(createPresetText).assertIsDisplayed()
        rule.onNodeWithContentDescription(presetDurationContentDescription).assertIsDisplayed()
        rule.onNodeWithText(okButtonText).assertIsDisplayed()
        rule.onNodeWithText(cancelButtonText).assertIsDisplayed()
    }

    @Test
    fun onPresetHoursUpdated_sendsUpdatesToVM() {
        mockState.value = mockState.value.copy(showCreatePresetDialog = true)

        val hoursSlot = slot<Int>()
        every { viewModel.onPresetHoursChanged(capture(hoursSlot)) } returns Unit

        val hours = 5
        val presetDurationPicker =
            rule.onNodeWithContentDescription(presetDurationContentDescription)
        presetDurationPicker.onChildren()
            .filterToOne(hasContentDescription(hoursContentDescription))
            .performScrollToIndex(getScrollPosition(24, hours))

        assertEquals(hours, hoursSlot.captured)
    }

    @Test
    fun onPresetMinutesUpdated_sendsUpdateToVM() {
        mockState.value = mockState.value.copy(showCreatePresetDialog = true)

        val minutesSlot = slot<Int>()
        every { viewModel.onPresetMinutesChanged(capture(minutesSlot)) } returns Unit

        val minutes = 30
        val presetDurationPicker =
            rule.onNodeWithContentDescription(presetDurationContentDescription)
        presetDurationPicker.onChildren()
            .filterToOne(hasContentDescription(minutesContentDescription))
            .performScrollToIndex(getScrollPosition(60, minutes))

        assertEquals(minutes, minutesSlot.captured)
    }

    @Test
    fun onPresetSecondsUpdated_sendsUpdateToVM() {
        mockState.value = mockState.value.copy(showCreatePresetDialog = true)

        val secondsSlot = slot<Int>()
        every { viewModel.onPresetSecondsChanged(capture(secondsSlot)) } returns Unit

        val seconds = 45
        val presetDurationPicker =
            rule.onNodeWithContentDescription(presetDurationContentDescription)
        presetDurationPicker.onChildren()
            .filterToOne(hasContentDescription(secondsContentDescription))
            .performScrollToIndex(getScrollPosition(60, seconds))

        assertEquals(seconds, secondsSlot.captured)
    }

    @Test
    fun onCancelButtonClicked_closesDialog() {
        mockState.value = mockState.value.copy(showCreatePresetDialog = true)
        every { viewModel.onCreatePresetDialogDismissed() } returns Unit

        rule.onNodeWithText(cancelButtonText).performClick()

        verify { viewModel.onCreatePresetDialogDismissed() }
    }

    @Test
    fun onOkButtonClicked_createsPreset() {
        mockState.value = mockState.value.copy(showCreatePresetDialog = true)
        every { viewModel.onCreatePresetDialogConfirmed() } returns Unit

        rule.onNodeWithText(okButtonText).performClick()

        verify { viewModel.onCreatePresetDialogConfirmed() }
    }

    @Test
    fun onPresetClicked_updatesDuration() {
        val presets = listOf(Preset(UUID.randomUUID(), 1.hours + 30.minutes + 25.seconds))
        mockState.value = mockState.value.copy(presets = presets)
        val presetSlot = slot<Preset>()
        every { viewModel.onPresetClicked(capture(presetSlot)) } returns Unit

        rule.onNodeWithText("01:30:25").performClick()

        assertEquals(presets.first(), presetSlot.captured)
    }

    @Test
    fun onPresetLongClicked_opensBottomSheet() {
        val presets = listOf(Preset(UUID.randomUUID(), 1.hours + 30.minutes + 25.seconds))
        mockState.value = mockState.value.copy(presets = presets)

        val presetSlot = slot<Preset>()
        every { viewModel.onPresetLongClicked(capture(presetSlot)) } returns Unit

        rule.onNodeWithText("01:30:25").performTouchInput { longClick() }

        assertEquals(presets.first(), presetSlot.captured)
    }

    @Test
    fun onPresetEditButtonClicked_opensEditDialog() = runTest {
        val presets = listOf(Preset(UUID.randomUUID(), 1.hours + 30.minutes + 25.seconds))
        mockState.value = mockState.value.copy(
            presets = presets,
            showEditPresetBottomSheet = true,
            presetInEdit = presets.first()
        )

        every { viewModel.onEditPresetButtonClicked() } returns Unit

        rule.onNodeWithText(editPresetButtonText).performClick()
        rule.mainClock.advanceTimeBy(1000L)

        verify { viewModel.onEditPresetButtonClicked() }
    }

    @Test
    fun onPresetDeleteButtonClicked_deletesPreset() = runTest {
        val presets = listOf(Preset(UUID.randomUUID(), 1.hours + 30.minutes + 25.seconds))
        mockState.value = mockState.value.copy(
            presets = presets,
            showEditPresetBottomSheet = true,
            presetInEdit = presets.first()
        )

        every { viewModel.onDeletePresetButtonClicked() } returns Unit

        rule.onNodeWithText(deletePresetButtonText).performClick()
        rule.mainClock.advanceTimeBy(1000L)

        verify { viewModel.onDeletePresetButtonClicked() }
    }

    private fun getScrollPosition(max: Int, index: Int) =
        Int.MAX_VALUE / 2 - (Int.MAX_VALUE / 2) % max - 1 + index
}