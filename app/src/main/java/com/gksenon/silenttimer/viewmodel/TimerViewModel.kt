package com.gksenon.silenttimer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class TimerViewModel : ViewModel() {

    private var tickerJob: Job? = null

    private val _state = MutableStateFlow<State>(State.Init())

    val state = _state.asStateFlow()

    fun onHoursChanged(input: String) {
        val state = _state.value as State.Init
        _state.value = state.copy(hours = validateDurationInput(input, 0..23))
    }

    fun onMinutesChanged(input: String) {
        val state = _state.value as State.Init
        _state.value = state.copy(minutes = validateDurationInput(input, 0..59))
    }

    fun onSecondsChanged(input: String) {
        val state = _state.value as State.Init
        _state.value = state.copy(seconds = validateDurationInput(input, 0..59))
    }

    fun onStartButtonClicked() {
        val state = _state.value as State.Init
        val duration = state.hours.hours + state.minutes.minutes + state.seconds.seconds
        _state.value = State.InProgress(remainingTime = duration)
        tickerJob = viewModelScope.launch {
            ticker(duration).collect {
                val state = _state.value as State.InProgress
                _state.value = state.copy(remainingTime = state.remainingTime - 1.seconds)
            }
            _state.value = State.Ringing
        }
    }

    fun onStopButtonClicked() {
        viewModelScope.launch {
            tickerJob?.cancelAndJoin()
            _state.value = State.Init()
        }
    }

    private fun ticker(duration: Duration) = flow {
        repeat(duration.inWholeSeconds.toInt()) {
            delay(1000L)
            emit(Unit)
        }
    }

    private fun validateDurationInput(input: String, range: IntRange) =
        input.filter { it.isDigit() }.toIntOrNull()?.coerceIn(range) ?: 0

    sealed class State {

        data class Init(val hours: Int = 0, val minutes: Int = 0, val seconds: Int = 0) : State()

        data class InProgress(val remainingTime: Duration) : State()

        object Ringing : State()
    }
}