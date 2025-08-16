package com.gksenon.ontime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class TimerViewModel : ViewModel() {

    private var tickerJob: Job? = null

    private val _state = MutableStateFlow<State>(State.Init)

    val state = _state.asStateFlow()

    fun onStartButtonClicked(hours: Int, minutes: Int, seconds: Int) {
        val duration = hours.hours + minutes.minutes + seconds.seconds
        _state.value = State.InProgress(remainingTime = duration)
        tickerJob = viewModelScope.launch {
            ticker().take(duration.toInt(DurationUnit.SECONDS)).collect {
                val state = _state.value as State.InProgress
                _state.value = state.copy(remainingTime = state.remainingTime - 1.seconds)
            }
            _state.value = State.Ringing()
            ticker().collect {
                val state = _state.value as State.Ringing
                _state.value = state.copy(timePassed = state.timePassed + 1.seconds)
            }
        }

    }

    fun onStopButtonClicked() = stopTimer()

    fun onTurnOffButtonClicked() = stopTimer()

    private fun ticker() = flow {
        while(true) {
            delay(1000L)
            emit(Unit)
        }
    }

    private fun stopTimer() {
        viewModelScope.launch {
            tickerJob?.cancelAndJoin()
            _state.value = State.Init
        }
    }

    sealed class State(val keepScreenOn: Boolean) {

        object Init : State(keepScreenOn = false)

        data class InProgress(val remainingTime: Duration) : State(keepScreenOn = true)

        data class Ringing(val timePassed: Duration = 0.seconds) : State(keepScreenOn = true)
    }
}