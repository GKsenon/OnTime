package com.gksenon.ontime

import com.gksenon.ontime.viewmodel.TimerRingingViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
class TimerRingingViewModelTest {

    @Before
    fun init() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun clean() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_showsTimePassed() = runTest {
        val viewModel = TimerRingingViewModel(TestClock(testScheduler))
        advanceTimeBy(0.5.seconds)

        val state = viewModel.state.value
        assertEquals(0.seconds, state.timePassed)
        assertFalse(state.navigateToInit)

        viewModel.onTurnOffButtonClicked()
    }

    @Test
    fun onTimePassed_updatesTime() = runTest {
        val viewModel = TimerRingingViewModel(TestClock(testScheduler))
        advanceTimeBy(1.5.seconds)

        val state = viewModel.state.value
        assertEquals(1.seconds, state.timePassed)

        viewModel.onTurnOffButtonClicked()
    }

    @Test
    fun onTurnOffButtonClicked_navigatesToInit() = runTest {
        val viewModel = TimerRingingViewModel(TestClock(testScheduler))
        viewModel.onTurnOffButtonClicked()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.navigateToInit)
    }
}