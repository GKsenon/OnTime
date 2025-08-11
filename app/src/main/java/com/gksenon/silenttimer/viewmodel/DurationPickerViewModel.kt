package com.gksenon.silenttimer.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DurationPickerViewModel : ViewModel() {

    private val _state = MutableStateFlow(State())

    val state = _state.asStateFlow()

    fun onHoursChanged(input: String) {
        _state.value = _state.value.copy(hours = validateDurationInput(input, 0 .. 23))
    }

    fun onMinutesChanged(input: String) {
        _state.value = _state.value.copy(minutes = validateDurationInput(input, 0 .. 59))
    }

    fun onSecondsChanged(input: String) {
        _state.value = _state.value.copy(seconds = validateDurationInput(input, 0 .. 59))
    }

    private fun validateDurationInput(input: String, range: IntRange) =
        input.filter { it.isDigit() }.toIntOrNull()?.coerceIn(range) ?: 0

    data class State(val hours: Int = 0, val minutes: Int = 0, val seconds: Int = 0)
}