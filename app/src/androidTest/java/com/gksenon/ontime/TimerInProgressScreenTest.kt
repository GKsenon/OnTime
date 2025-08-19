package com.gksenon.ontime

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.gksenon.ontime.ui.TimerInProgressScreen
import com.gksenon.ontime.viewmodel.TimerInProgressViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class TimerInProgressScreenTest {

    @get:Rule
    val rule = createComposeRule()

    private val duration = 1.hours + 30.minutes + 45.seconds
    private val mockState = MutableStateFlow(TimerInProgressViewModel.State(remainingTime = duration))
    private val viewModel = mockk<TimerInProgressViewModel> {
        every { state } returns mockState
    }
    private val navigateToInit: () -> Unit = mockk()
    private val navigateToRinging: () -> Unit = mockk()

    private lateinit var stopButtonText: String

    @Before
    fun before() {
        rule.setContent {
            TimerInProgressScreen(
                viewModel = viewModel,
                navigateToInitScreen = navigateToInit,
                navigateToRingingScreen = navigateToRinging
            )
            stopButtonText = stringResource(R.string.stop)
        }
    }

    @Test
    fun init_displaysRemainingTime() {
        rule.onNodeWithText("01:30:45").assertIsDisplayed()
    }

    @Test
    fun onRemainingTimeUpdated_displaysRemainingTime() {
        mockState.value = mockState.value.copy(remainingTime = duration - 1.seconds)
        rule.onNodeWithText("01:30:44").assertIsDisplayed()
    }

    @Test
    fun onStopButtonClicked_navigatesToInit() {
        every { viewModel.onStopButtonClicked() } returns Unit
        every { navigateToInit() } returns Unit

        rule.onNodeWithContentDescription(stopButtonText).performClick()
        verify { viewModel.onStopButtonClicked() }

        mockState.value = mockState.value.copy(navigateToInit = true)
        rule.mainClock.advanceTimeBy(1000L)

        verify { navigateToInit() }
    }

    @Test
    fun onTimeRanOut_navigatesToRinging() {
        every { navigateToRinging() } returns Unit

        mockState.value = mockState.value.copy(remainingTime = 0.seconds, navigateToRinging = true)
        rule.mainClock.advanceTimeBy(1000L)

        verify { navigateToRinging() }
    }
}