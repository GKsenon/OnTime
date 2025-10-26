package com.gksenon.ontime

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.gksenon.ontime.ui.TimerRingingScreen
import com.gksenon.ontime.viewmodel.TimerRingingViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class TimerRingingScreenTest {

    @get:Rule
    val rule = createComposeRule()

    private val mockkState = MutableStateFlow(TimerRingingViewModel.State())
    private val viewModel = mockk<TimerRingingViewModel> {
        every { state } returns mockkState
    }
    private val navigateToInit: () -> Unit = mockk()

    private lateinit var turnOffButtonText: String

    @Before
    fun before() {
        rule.setContent {
            TimerRingingScreen(viewModel = viewModel, navigateToInit = navigateToInit)
            turnOffButtonText = stringResource(R.string.turn_off)
        }
    }

    @Test
    fun init_displaysTimePassed() {
        rule.onNodeWithText("-00:00:00").assertIsDisplayed()
    }

    @Test
    fun onTimePassed_displaysNewTime() {
        mockkState.value = mockkState.value.copy(timePassed = 5.seconds)
        rule.onNodeWithText("-00:00:05").assertIsDisplayed()
    }

    @Test
    fun onTurnOffButtonClicked_navigatesToInit() {
        every { viewModel.onTurnOffButtonClicked() } returns Unit
        every { navigateToInit() } returns Unit

        rule.onNodeWithText(turnOffButtonText).performClick()
        verify { viewModel.onTurnOffButtonClicked() }

        mockkState.value = mockkState.value.copy(navigateToInit = true)
        rule.mainClock.advanceTimeBy(1000L)

        verify { navigateToInit() }
    }
}