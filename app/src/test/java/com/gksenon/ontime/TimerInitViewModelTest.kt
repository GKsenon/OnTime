package com.gksenon.ontime

import com.gksenon.ontime.domain.Preset
import com.gksenon.ontime.domain.TimerRepository
import com.gksenon.ontime.viewmodel.TimerInitViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class TimerInitViewModelTest {

    private val presets = listOf(
        Preset(UUID.randomUUID(), 30.seconds),
        Preset(UUID.randomUUID(), 1.hours + 30.minutes + 25.seconds)
    )
    private val repository = mockk<TimerRepository>() {
        every { getPresets() } returns flowOf(presets)
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
    fun init_showsPresets() = runTest {
        val viewModel = TimerInitViewModel(repository)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(0.seconds, state.duration)
        assertEquals(presets, state.presets)
        assertFalse(state.showCreatePresetDialog)
        assertEquals(null, state.selectedPresetId)
        assertEquals(0.seconds, state.presetDuration)
        assertFalse(state.navigateToTimerInProgress)
    }

    @Test
    fun onHoursChanged_updatesHours() = runTest {
        val viewModel = TimerInitViewModel(repository)
        viewModel.onHoursChanged(1)
        viewModel.onMinutesChanged(30)
        viewModel.onSecondsChanged(30)
        advanceUntilIdle()

        viewModel.onHoursChanged(2)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(2.hours + 30.minutes + 30.seconds, state.duration)
    }

    @Test
    fun onMinutesChanged_updatesMinutes() = runTest {
        val viewModel = TimerInitViewModel(repository)
        viewModel.onHoursChanged(1)
        viewModel.onMinutesChanged(30)
        viewModel.onSecondsChanged(30)
        advanceUntilIdle()

        viewModel.onMinutesChanged(25)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(1.hours + 25.minutes + 30.seconds, state.duration)
    }

    @Test
    fun onSecondsChanged_updatesSeconds() = runTest {
        val viewModel = TimerInitViewModel(repository)
        viewModel.onHoursChanged(1)
        viewModel.onMinutesChanged(30)
        viewModel.onSecondsChanged(30)
        advanceUntilIdle()

        viewModel.onSecondsChanged(15)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(1.hours + 30.minutes + 15.seconds, state.duration)
    }

    @Test
    fun onPresetClicked_updatesTimerDuration() = runTest {
        val viewModel = TimerInitViewModel(repository)
        advanceUntilIdle()

        viewModel.onPresetClicked(presets.first())
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(presets.first().duration, state.duration)
        assertEquals(presets.first().id, state.selectedPresetId)
    }

    @Test
    fun onCreatePresetButtonClicked_showsCreatePresetDialog() = runTest {
        val viewModel = TimerInitViewModel(repository)
        advanceUntilIdle()

        viewModel.onCreatePresetButtonClicked()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.showCreatePresetDialog)
    }

    @Test
    fun onPresetHoursChanged_updatesPresetHours() = runTest {
        val viewModel = TimerInitViewModel(repository)
        viewModel.onCreatePresetButtonClicked()
        viewModel.onPresetHoursChanged(1)
        viewModel.onPresetMinutesChanged(30)
        viewModel.onPresetSecondsChanged(15)
        advanceUntilIdle()

        viewModel.onPresetHoursChanged(2)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(2.hours + 30.minutes + 15.seconds, state.presetDuration)
    }

    @Test
    fun onPresetMinutesChanged_updatesPresetMinutes() = runTest {
        val viewModel = TimerInitViewModel(repository)
        viewModel.onCreatePresetButtonClicked()
        viewModel.onPresetHoursChanged(1)
        viewModel.onPresetMinutesChanged(30)
        viewModel.onPresetSecondsChanged(15)
        advanceUntilIdle()

        viewModel.onPresetMinutesChanged(45)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(1.hours + 45.minutes + 15.seconds, state.presetDuration)
    }

    @Test
    fun onPresetSecondsChanged_updatesPresetSeconds() = runTest {
        val viewModel = TimerInitViewModel(repository)
        viewModel.onCreatePresetButtonClicked()
        viewModel.onPresetHoursChanged(1)
        viewModel.onPresetMinutesChanged(30)
        viewModel.onPresetSecondsChanged(15)
        advanceUntilIdle()

        viewModel.onPresetSecondsChanged(50)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(1.hours + 30.minutes + 50.seconds, state.presetDuration)
    }

    @Test
    fun onCreatePresetDialogConfirmed_createsPreset() = runTest {
        val durationSlot = slot<Duration>()
        coEvery { repository.savePreset(capture(durationSlot)) } returns Unit
        val viewModel = TimerInitViewModel(repository)
        viewModel.onCreatePresetButtonClicked()
        viewModel.onPresetHoursChanged(1)
        viewModel.onPresetMinutesChanged(30)
        viewModel.onPresetSecondsChanged(15)
        advanceUntilIdle()

        viewModel.onCreatePresetDialogConfirmed()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.showCreatePresetDialog)
        assertEquals(0.seconds, state.presetDuration)
        assertEquals(1.hours + 30.minutes + 15.seconds, durationSlot.captured)
    }

    @Test
    fun onCreatePresetDialogDismissed_closesDialog() = runTest {
        val viewModel = TimerInitViewModel(repository)
        viewModel.onCreatePresetButtonClicked()
        viewModel.onPresetHoursChanged(1)
        viewModel.onPresetMinutesChanged(30)
        viewModel.onPresetSecondsChanged(15)
        advanceUntilIdle()

        viewModel.onCreatePresetDialogDismissed()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.showCreatePresetDialog)
        assertEquals(0.seconds, state.presetDuration)
        coVerify(exactly = 0) { repository.savePreset(any()) }
    }

    @Test
    fun onStartButtonClicked_startsTimer() = runTest {
        val viewModel = TimerInitViewModel(repository)
        viewModel.onHoursChanged(1)
        viewModel.onMinutesChanged(30)
        viewModel.onSecondsChanged(30)
        advanceUntilIdle()

        viewModel.onStartButtonClicked()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.navigateToTimerInProgress)
    }
}
