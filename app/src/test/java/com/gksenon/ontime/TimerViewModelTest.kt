package com.gksenon.ontime

import com.gksenon.ontime.viewmodel.TimerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class TimerViewModelTest {

    private val viewModel = TimerViewModel()

    @Before
    fun init() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun clean() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_showsDefaultValue() = runTest {
        assert(viewModel.state.value is TimerViewModel.State.Init)
    }

    @Test
    fun onStartButtonClicked_startsTimer() = runTest {
        viewModel.onStartButtonClicked(hours = 1, minutes = 3, seconds = 5)

        val duration = 1.hours + 3.minutes + 5.seconds
        assertEquals(
            duration,
            (viewModel.state.value as TimerViewModel.State.InProgress).remainingTime
        )

        advanceTimeBy(1500L)
        assertEquals(
            duration - 1.seconds,
            (viewModel.state.value as TimerViewModel.State.InProgress).remainingTime
        )

        viewModel.onStopButtonClicked()
    }

    @Test
    fun onTimeRanOut_startsRinging() = runTest {
        viewModel.onStartButtonClicked(hours = 0, minutes = 0, seconds = 5)

        assert(viewModel.state.value is TimerViewModel.State.InProgress)

        advanceTimeBy(5500L)
        assert(viewModel.state.value is TimerViewModel.State.Ringing)

        viewModel.onTurnOffButtonClicked()
    }

    @Test
    fun onTimeRanOut_showsTimePassed() = runTest {
        viewModel.onStartButtonClicked(hours = 0, minutes = 0, seconds = 2)
        advanceTimeBy(2500L)
        assert(viewModel.state.value is TimerViewModel.State.Ringing)
        assertEquals(
            0.seconds,
            (viewModel.state.value as TimerViewModel.State.Ringing).timePassed
        )

        advanceTimeBy(1000L)
        assertEquals(
            1.seconds,
            (viewModel.state.value as TimerViewModel.State.Ringing).timePassed
        )

        viewModel.onTurnOffButtonClicked()
    }

    @Test
    fun onStopButtonClicked_stopsTimer() = runTest {
        viewModel.onStartButtonClicked(hours = 0, minutes = 0, seconds = 5)
        advanceTimeBy(1500L)
        viewModel.onStopButtonClicked()
        advanceUntilIdle()
        assert(viewModel.state.value is TimerViewModel.State.Init)
    }

    @Test
    fun onTurnOffButtonClicked_resetsStateToInit() = runTest {
        viewModel.onStartButtonClicked(hours = 0, minutes = 0, seconds = 1)
        advanceTimeBy(2.seconds)
        viewModel.onTurnOffButtonClicked()
        advanceUntilIdle()
        assert(viewModel.state.value is TimerViewModel.State.Init)
    }
}
