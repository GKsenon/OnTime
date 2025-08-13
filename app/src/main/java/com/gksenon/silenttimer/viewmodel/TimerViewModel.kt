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

    private val _state = MutableStateFlow<State>(State.Init)

    val state = _state.asStateFlow()

    fun onStartButtonClicked(hours: Int, minutes: Int, seconds: Int) {
        val duration = hours.hours + minutes.minutes + seconds.seconds
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
            _state.value = State.Init
        }
    }

    fun onMuteButtonClicked() {
        _state.value = State.Init
    }

    private fun ticker(duration: Duration) = flow {
        repeat(duration.inWholeSeconds.toInt()) {
            delay(1000L)
            emit(Unit)
        }
    }

    sealed class State(val keepScreenOn: Boolean) {

        object Init : State(keepScreenOn = false)

        data class InProgress(val remainingTime: Duration) : State(keepScreenOn = true)

        object Ringing : State(keepScreenOn = true)
    }
}