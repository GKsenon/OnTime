package com.gksenon.silenttimer

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gksenon.silenttimer.ui.TimerScreen
import com.gksenon.silenttimer.ui.theme.SilentTimerTheme
import com.gksenon.silenttimer.viewmodel.TimerViewModel
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

    private val timerState = MutableStateFlow<TimerViewModel.State>(TimerViewModel.State.Init())
    private val viewModel = mockk<TimerViewModel> {
        every { state } returns timerState
    }

    private lateinit var hoursContentDescription: String
    private lateinit var minutesContentDescription: String
    private lateinit var secondsContentDescription: String
    private lateinit var startButtonContentDescription: String
    private lateinit var stopButtonContentDescription: String
    private lateinit var ringingIndicatorContentDescription: String

    @Before
    fun init() {
        rule.setContent {
            SilentTimerTheme {
                hoursContentDescription = stringResource(R.string.hours_content_description)
                minutesContentDescription = stringResource(R.string.minutes_content_description)
                secondsContentDescription = stringResource(R.string.seconds_content_description)
                startButtonContentDescription = stringResource(R.string.start)
                stopButtonContentDescription = stringResource(R.string.stop)
                ringingIndicatorContentDescription = stringResource(R.string.timer_ringing_indicator)
                TimerScreen(viewModel = viewModel)
            }
        }
    }

    @Test
    fun init_showsDefaultState() {
        rule.onNodeWithContentDescription(hoursContentDescription).assertIsDisplayed()
        rule.onNodeWithContentDescription(minutesContentDescription).assertIsDisplayed()
        rule.onNodeWithContentDescription(secondsContentDescription).assertIsDisplayed()
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
    fun onStartButtonClicked_startsTimer() {
        every { viewModel.onStartButtonClicked() } returns Unit
        rule.onNodeWithContentDescription(startButtonContentDescription).performClick()
        verify { viewModel.onStartButtonClicked() }
    }

    @Test
    fun onTimerInProgress_showsRemainingTime() {
        timerState.value = TimerViewModel.State.InProgress(1.hours + 30.minutes + 5.seconds)
        rule.onNodeWithText("01:30:05").assertIsDisplayed()
        rule.onNodeWithContentDescription(stopButtonContentDescription).assertIsDisplayed()
    }

    @Test
    fun onStopButtonClicked_stopsTimer() {
        every { viewModel.onStopButtonClicked() } returns Unit
        timerState.value = TimerViewModel.State.InProgress(5.seconds)
        rule.onNodeWithContentDescription(stopButtonContentDescription).performClick()
        verify { viewModel.onStopButtonClicked() }
    }

    @Test
    fun onTimerRinging_showsColoredScreen() {
        timerState.value = TimerViewModel.State.Ringing
        rule.onNodeWithContentDescription(ringingIndicatorContentDescription).assertIsDisplayed()
    }
}