package com.gksenon.ontime

import android.app.Activity
import android.view.Window
import android.view.WindowManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gksenon.ontime.ui.TimerScreen
import com.gksenon.ontime.ui.theme.OnTimeTheme
import com.gksenon.ontime.viewmodel.TimerViewModel
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
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@RunWith(AndroidJUnit4::class)
class TimerScreenTest {

    @get:Rule
    val rule = createComposeRule()

    private val timerState = MutableStateFlow<TimerViewModel.State>(TimerViewModel.State.Init)
    private val viewModel = mockk<TimerViewModel> {
        every { state } returns timerState
    }

    private lateinit var hoursContentDescription: String
    private lateinit var minutesContentDescription: String
    private lateinit var secondsContentDescription: String
    private lateinit var startButtonContentDescription: String
    private lateinit var stopButtonContentDescription: String
    private lateinit var timeRanOutText: String
    private lateinit var turnOffButtonText: String
    private lateinit var windowManager: Window

    @Before
    fun init() {
        rule.setContent {
            OnTimeTheme {
                hoursContentDescription = stringResource(R.string.hours_content_description)
                minutesContentDescription = stringResource(R.string.minutes_content_description)
                secondsContentDescription = stringResource(R.string.seconds_content_description)
                startButtonContentDescription = stringResource(R.string.start)
                stopButtonContentDescription = stringResource(R.string.stop)
                timeRanOutText = stringResource(R.string.time_ran_out)
                turnOffButtonText = stringResource(R.string.turn_off)
                windowManager = (LocalContext.current as Activity).window
                TimerScreen(viewModel = viewModel)
            }
        }
    }

    @Test
    fun init_showsDefaultState() {
        rule.onNodeWithContentDescription(hoursContentDescription).assertIsDisplayed()
        rule.onNodeWithContentDescription(minutesContentDescription).assertIsDisplayed()
        rule.onNodeWithContentDescription(secondsContentDescription).assertIsDisplayed()
        assert((windowManager.attributes.flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) == 0)
    }

    @Test
    fun onStartButtonClicked_startsTimer() {
        val hoursSlot = slot<Int>()
        val minutesSlot = slot<Int>()
        val secondsSlot = slot<Int>()
        every {
            viewModel.onStartButtonClicked(
                capture(hoursSlot),
                capture(minutesSlot),
                capture(secondsSlot)
            )
        } returns Unit

        val hours = 1
        val minutes = 3
        val seconds = 5
        rule.onNodeWithContentDescription(hoursContentDescription)
            .performScrollToIndex(getScrollPosition(24, hours))
        rule.onNodeWithContentDescription(minutesContentDescription)
            .performScrollToIndex(getScrollPosition(60, minutes))
        rule.onNodeWithContentDescription(secondsContentDescription)
            .performScrollToIndex(getScrollPosition(60, seconds))
        rule.onNodeWithContentDescription(startButtonContentDescription).performClick()
        assertEquals(hours, hoursSlot.captured)
        assertEquals(minutes, minutesSlot.captured)
        assertEquals(seconds, secondsSlot.captured)
    }

    @Test
    fun onTimerInProgress_showsRemainingTime() {
        timerState.value = TimerViewModel.State.InProgress(1.hours + 30.minutes + 5.seconds)
        rule.onNodeWithText("01:30:05").assertIsDisplayed()
        rule.onNodeWithContentDescription(stopButtonContentDescription).assertIsDisplayed()
        assert((windowManager.attributes.flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) != 0)
    }

    @Test
    fun onStopButtonClicked_stopsTimer() {
        every { viewModel.onStopButtonClicked() } returns Unit
        timerState.value = TimerViewModel.State.InProgress(5.seconds)
        rule.onNodeWithContentDescription(stopButtonContentDescription).performClick()
        verify { viewModel.onStopButtonClicked() }
    }

    @Test
    fun onTimerRinging_showsIndicator() {
        timerState.value = TimerViewModel.State.Ringing
        rule.onNodeWithText(timeRanOutText).assertIsDisplayed()
        rule.onNodeWithText(turnOffButtonText).assertIsDisplayed()
        assert((windowManager.attributes.flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) != 0)
    }

    @Test
    fun onTurnOffButtonClicked_resetsTimer() {
        every { viewModel.onTurnOffButtonClicked() } returns Unit
        timerState.value = TimerViewModel.State.Ringing
        rule.onNodeWithText(turnOffButtonText).performClick()
        verify { viewModel.onTurnOffButtonClicked() }
    }

    private fun getScrollPosition(max: Int, index: Int) =
        Int.MAX_VALUE / 2 - (Int.MAX_VALUE / 2) % max - 1 + index
}