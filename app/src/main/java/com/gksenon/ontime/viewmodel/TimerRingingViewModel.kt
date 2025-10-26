package com.gksenon.ontime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gksenon.ontime.domain.Clock
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
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@HiltViewModel
class TimerRingingViewModel @Inject constructor(
    private val clock: Clock
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private val startedAt = clock.now()

    private var tickerJob: Job? = null

    init {
        tickerJob = viewModelScope.launch {
            ticker().collect {
                val timePassed = clock.now().minus(startedAt)
                _state.value = _state.value.copy(timePassed = timePassed)
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
            delay(1.seconds)
            emit(Unit)
        }
    }

    data class State(
        val timePassed: Duration = 0.seconds,
        val navigateToInit: Boolean = false
    )
}