package com.gksenon.silenttimer

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gksenon.silenttimer.ui.DurationPickerScreen
import com.gksenon.silenttimer.ui.theme.SilentTimerTheme
import com.gksenon.silenttimer.viewmodel.DurationPickerViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DurationPickerScreenTest {

    @get:Rule
    val rule = createComposeRule()

    private val durationPickerState = MutableStateFlow(DurationPickerViewModel.State())
    private val viewModel = mockk<DurationPickerViewModel> {
        every { state } returns durationPickerState
    }
    private val startCallback = mockk<(Int) -> Unit>()

    private lateinit var hoursContentDescription: String
    private lateinit var minutesContentDescription: String
    private lateinit var secondsContentDescription: String
    private lateinit var startButtonContentDescription: String

    @Before
    fun init() {
        every { startCallback(any()) } returns Unit
        rule.setContent {
            SilentTimerTheme {
                hoursContentDescription = stringResource(R.string.hours_content_description)
                minutesContentDescription = stringResource(R.string.minutes_content_description)
                secondsContentDescription = stringResource(R.string.seconds_content_description)
                startButtonContentDescription = stringResource(R.string.start)
                DurationPickerScreen(viewModel = viewModel, onStartTimer = startCallback)
            }
        }
    }

    @Test
    fun init_showsDefaultState() {
        rule.onNodeWithContentDescription(hoursContentDescription).assertTextEquals("0")
        rule.onNodeWithContentDescription(minutesContentDescription).assertTextEquals("0")
        rule.onNodeWithContentDescription(secondsContentDescription).assertTextEquals("0")
    }

    @Test
    fun onHoursChanged_sendsUpdateToViewModel() {
        val hoursSlot = slot<String>()
        every { viewModel.onHoursChanged(capture(hoursSlot)) } returns Unit

        rule.onNodeWithContentDescription(hoursContentDescription).performTextReplacement("5")
        assertEquals("5", hoursSlot.captured)
    }

    @Test
    fun onMinutesChanged_sendsUpdateToViewModel() {
        val minutesSlot = slot<String>()
        every { viewModel.onMinutesChanged(capture(minutesSlot)) } returns Unit

        rule.onNodeWithContentDescription(minutesContentDescription).performTextReplacement("25")
        assertEquals("25", minutesSlot.captured)
    }

    @Test
    fun onSecondsChanged_sendsUpdateToViewModel() {
        val secondsSlot = slot<String>()
        every { viewModel.onSecondsChanged(capture(secondsSlot)) } returns Unit

        rule.onNodeWithContentDescription(secondsContentDescription).performTextReplacement("25")
        assertEquals("25", secondsSlot.captured)
    }

    @Test
    fun onStartButtonClicked_startsTimes() {
        rule.onNodeWithContentDescription(startButtonContentDescription).performClick()
        verify { startCallback(any()) }
    }
}