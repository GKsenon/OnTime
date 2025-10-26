package com.gksenon.ontime

import androidx.lifecycle.SavedStateHandle
import com.gksenon.ontime.domain.Flag
import com.gksenon.ontime.domain.FlagsRepository
import com.gksenon.ontime.viewmodel.TimerInProgressViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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

@OptIn(ExperimentalCoroutinesApi::class)
class TimerInProgressViewModelTest {

    private val savedStateHandle = SavedStateHandle().apply {
        set("duration", 90L)
    }
    private val flagsFlow = MutableStateFlow(
        listOf(
            Flag(
                id = 1,
                duration = 1.seconds,
            ), Flag(
                id = 2,
                duration = 2.seconds
            )
        )
    )
    private val flagRepository = mockk<FlagsRepository> {
        every { start() } returns Unit
        every { flags } returns flagsFlow
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
        val viewModel = TimerInProgressViewModel(savedStateHandle, flagRepository)
        advanceTimeBy(500L)

        val state = viewModel.state.value
        assertEquals(90.seconds, state.remainingTime)
        assertEquals(flagsFlow.value, state.flags)
        assertFalse(state.navigateToRinging)
        assertFalse(state.navigateToInit)
    }

    @Test
    fun givenDurationIsZero_navigatesToRinging() = runTest {
        val savedStateHandleWithZeroDuration = SavedStateHandle().apply {
            set("duration", 0L)
        }
        val viewModel = TimerInProgressViewModel(savedStateHandleWithZeroDuration, flagRepository)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.navigateToRinging)
    }

    @Test
    fun onTimePassed_updatesRemainingTime() = runTest {
        val viewModel = TimerInProgressViewModel(savedStateHandle, flagRepository)
        advanceTimeBy(1500L)

        val state = viewModel.state.value
        assertEquals(89.seconds, state.remainingTime)
    }

    @Test
    fun onFlagButtonClicked_createsFlag() = runTest {
        every { flagRepository.saveFlag() } returns Unit
        val viewModel = TimerInProgressViewModel(savedStateHandle, flagRepository)
        viewModel.onFlagButtonClicked()
        val newFlags = flagsFlow.value.plus(Flag(id = 3, duration = 3.seconds))
        flagsFlow.value = newFlags
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(state.flags, newFlags)
        verify { flagRepository.saveFlag() }
    }

    @Test
    fun onTimeRanOut_navigatesToRinging() = runTest {
        val viewModel = TimerInProgressViewModel(savedStateHandle, flagRepository)
        advanceTimeBy(90500L)

        val state = viewModel.state.value
        assertTrue(state.navigateToRinging)
    }

    @Test
    fun onStopButtonClicked_navigatesToInit() = runTest {
        val viewModel = TimerInProgressViewModel(savedStateHandle, flagRepository)
        viewModel.onStopButtonClicked()
        advanceTimeBy(500L)

        val state = viewModel.state.value
        assertTrue(state.navigateToInit)
    }
}