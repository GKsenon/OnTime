package com.gksenon.silenttimer

import com.gksenon.silenttimer.viewmodel.TimerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration
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
        val state = viewModel.state.value as TimerViewModel.State.Init
        assertEquals(0, state.hours)
        assertEquals(0, state.minutes)
        assertEquals(0, state.seconds)
    }

    @Test
    fun onHoursChanged_validatesHoursAndUpdatesState() = runTest {
        viewModel.onHoursChanged("-8")
        assertEquals(8, (viewModel.state.value as TimerViewModel.State.Init).hours)
        viewModel.onHoursChanged("2abs-=,.1")
        assertEquals(21, (viewModel.state.value as TimerViewModel.State.Init).hours)
        viewModel.onHoursChanged("45")
        assertEquals(23, (viewModel.state.value as TimerViewModel.State.Init).hours)
    }

    @Test
    fun onMinutesChanged_validatesMinutesAndUpdatesState() = runTest {
        viewModel.onMinutesChanged("-8")
        assertEquals(8, (viewModel.state.value as TimerViewModel.State.Init).minutes)
        viewModel.onMinutesChanged("2abs-=,.1")
        assertEquals(21, (viewModel.state.value as TimerViewModel.State.Init).minutes)
        viewModel.onMinutesChanged("120")
        assertEquals(59, (viewModel.state.value as TimerViewModel.State.Init).minutes)
    }

    @Test
    fun onSecondsChanged_validatesSecondsAndUpdatesState() = runTest {
        viewModel.onSecondsChanged("-8")
        assertEquals(8, (viewModel.state.value as TimerViewModel.State.Init).seconds)
        viewModel.onSecondsChanged("2abs-=,.1")
        assertEquals(21, (viewModel.state.value as TimerViewModel.State.Init).seconds)
        viewModel.onSecondsChanged("112")
        assertEquals(59, (viewModel.state.value as TimerViewModel.State.Init).seconds)
    }

    @Test
    fun onStartButtonClicked_startsTimer() = runTest {
        viewModel.onHoursChanged("1")
        viewModel.onMinutesChanged("3")
        viewModel.onSecondsChanged("5")
        viewModel.onStartButtonClicked()

        val duration = 1.hours + 3.minutes + 5.seconds
        assertEquals(
            duration,
            (viewModel.state.value as TimerViewModel.State.InProgress).remainingTime
        )

        advanceTimeBy(1500L)
        assertEquals(duration - 1.seconds, (viewModel.state.value as TimerViewModel.State.InProgress).remainingTime)
    }

    @Test
    fun onTimeRanOut_startsRinging() = runTest {
        viewModel.onSecondsChanged("5")
        viewModel.onStartButtonClicked()

        assert(viewModel.state.value is TimerViewModel.State.InProgress)

        advanceTimeBy(5500L)
        assert(viewModel.state.value is TimerViewModel.State.Ringing)
    }

    @Test
    fun onStopButtonClicked_stopsTimer() = runTest {
        viewModel.onSecondsChanged("5")
        viewModel.onStartButtonClicked()
        advanceTimeBy(1500L)
        viewModel.onStopButtonClicked()
        advanceUntilIdle()
        assert(viewModel.state.value is TimerViewModel.State.Init)
    }
}
