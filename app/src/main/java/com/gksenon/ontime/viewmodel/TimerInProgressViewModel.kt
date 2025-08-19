package com.gksenon.ontime.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class TimerInProgressViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val duration = savedStateHandle.get<Long>("duration")?.seconds ?: 0.seconds

    private val _state = MutableStateFlow(State(duration))
    val state = _state.asStateFlow()

    private var tickerJob: Job? = null

    init {
        tickerJob = viewModelScope.launch {
            ticker(duration).collect {
                val remainingTime = _state.value.remainingTime - 1.seconds
                _state.value = _state.value.copy(
                    remainingTime = remainingTime,
                    navigateToRinging = remainingTime <= 0.seconds
                )
            }
        }
    }

    fun onStopButtonClicked() {
        viewModelScope.launch {
            tickerJob?.cancelAndJoin()
            _state.value = _state.value.copy(navigateToInit = true)
        }
    }

    private fun ticker(duration: Duration) = flow {
        repeat(duration.inWholeSeconds.toInt()) {
            delay(1000L)
            emit(Unit)
        }
    }

    data class State(
        val remainingTime: Duration = 0.seconds,
        val navigateToInit: Boolean = false,
        val navigateToRinging: Boolean = false
    )
}
