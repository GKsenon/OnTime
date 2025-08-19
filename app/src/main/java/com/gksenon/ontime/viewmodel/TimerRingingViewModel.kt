package com.gksenon.ontime.viewmodel

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
class TimerRingingViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private var tickerJob: Job? = null

    init {
        tickerJob = viewModelScope.launch {
            ticker().collect {
                val timePassed = _state.value.timePassed + 1.seconds
                _state.value = _state.value.copy(timePassed)
            }
        }
    }

    fun onTurnOffButtonClicked() {
        viewModelScope.launch {
            tickerJob?.cancelAndJoin()
            _state.value = _state.value.copy(navigateToInit = true)
        }
    }

    private fun ticker() = flow {
        while (true) {
            delay(1000L)
            emit(Unit)
        }
    }

    data class State(val timePassed: Duration = 0.seconds, val navigateToInit: Boolean = false)
}