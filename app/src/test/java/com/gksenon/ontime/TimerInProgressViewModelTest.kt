package com.gksenon.ontime

import androidx.lifecycle.SavedStateHandle
import com.gksenon.ontime.viewmodel.TimerInProgressViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class TimerInProgressViewModelTest {

    private val savedStateHandle = SavedStateHandle().apply {
        set("duration", 90L)
    }

    @Before
    fun init() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun clean() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_showsTimerDuration() = runTest {
        val viewModel = TimerInProgressViewModel(savedStateHandle)
        advanceTimeBy(500L)

        val state = viewModel.state.value
        assertEquals(90.seconds, state.remainingTime)
        assertFalse(state.navigateToRinging)
        assertFalse(state.navigateToInit)
    }

    @Test
    fun onTimePassed_updatesRemainingTime() = runTest {
        val viewModel = TimerInProgressViewModel(savedStateHandle)
        advanceTimeBy(1500L)

        val state = viewModel.state.value
        assertEquals(89.seconds, state.remainingTime)
    }

    @Test
    fun onTimeRanOut_navigatesToRinging() = runTest {
        val viewModel = TimerInProgressViewModel(savedStateHandle)
        advanceTimeBy(90500L)

        val state = viewModel.state.value
        assertTrue(state.navigateToRinging)
    }

    @Test
    fun onStopButtonClicked_navigatesToInit() = runTest {
        val viewModel = TimerInProgressViewModel(savedStateHandle)
        viewModel.onStopButtonClicked()
        advanceTimeBy(500L)

        val state = viewModel.state.value
        assertTrue(state.navigateToInit)
    }
}