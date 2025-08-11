package com.gksenon.silenttimer

import com.gksenon.silenttimer.viewmodel.DurationPickerViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DurationPickerViewModelTest {

    private val viewModel = DurationPickerViewModel()

    @Test
    fun init_showsDefaultValue() = runTest {
        val state = viewModel.state.value
        assertEquals(0, state.hours)
        assertEquals(0, state.minutes)
        assertEquals(0, state.seconds)
    }

    @Test
    fun onHoursChanged_validatesHoursAndUpdatesState() = runTest {
        viewModel.onHoursChanged("-8")
        assertEquals(8, viewModel.state.value.hours)
        viewModel.onHoursChanged("2abs-=,.1")
        assertEquals(21, viewModel.state.value.hours)
        viewModel.onHoursChanged("45")
        assertEquals(23, viewModel.state.value.hours)
    }

    @Test
    fun onMinutesChanged_validatesMinutesAndUpdatesState() = runTest {
        viewModel.onMinutesChanged("-8")
        assertEquals(8, viewModel.state.value.minutes)
        viewModel.onMinutesChanged("2abs-=,.1")
        assertEquals(21, viewModel.state.value.minutes)
        viewModel.onMinutesChanged("120")
        assertEquals(59, viewModel.state.value.minutes)
    }

    @Test
    fun onSecondsChanged_validatesSecondsAndUpdatesState() = runTest {
        viewModel.onSecondsChanged("-8")
        assertEquals(8, viewModel.state.value.seconds)
        viewModel.onSecondsChanged("2abs-=,.1")
        assertEquals(21, viewModel.state.value.seconds)
        viewModel.onSecondsChanged("112")
        assertEquals(59, viewModel.state.value.seconds)
    }
}
