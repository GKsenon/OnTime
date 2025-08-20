package com.gksenon.ontime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gksenon.ontime.domain.Preset
import com.gksenon.ontime.domain.TimerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class TimerInitViewModel @Inject constructor(private val repository: TimerRepository) :
    ViewModel() {

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getPresets()
                .collect { presets -> _state.value = _state.value.copy(presets = presets) }
        }
    }

    fun onHoursChanged(hours: Int) {
        _state.value = _state.value.copy(duration = _state.value.duration.copy(hours = hours))
    }

    fun onMinutesChanged(minutes: Int) {
        _state.value = _state.value.copy(duration = _state.value.duration.copy(minutes = minutes))
    }

    fun onSecondsChanged(seconds: Int) {
        _state.value = _state.value.copy(duration = _state.value.duration.copy(seconds = seconds))
    }

    fun onPresetClicked(preset: Preset) {
        _state.value = _state.value.copy(duration = preset.duration, selectedPresetId = preset.id)
    }

    fun onPresetLongClicked(preset: Preset) {
        _state.value = _state.value.copy(showEditPresetBottomSheet = true, presetInEdit = preset)
    }

    fun onEditPresetButtonClicked() {
        _state.value.presetInEdit?.let { presetInEdit ->
            _state.value = _state.value.copy(
                showEditPresetBottomSheet = false,
                showCreatePresetDialog = true,
                presetDuration = presetInEdit.duration
            )
        }
    }

    fun onDeletePresetButtonClicked() {
        val preset = _state.value.presetInEdit
        _state.value = _state.value.copy(showEditPresetBottomSheet = false, presetInEdit = null)
        if(preset != null) viewModelScope.launch { repository.deletePreset(preset) }
    }

    fun onPresetBottomSheetDismissed() {
        _state.value = _state.value.copy(showEditPresetBottomSheet = false, presetInEdit = null)
    }

    fun onCreatePresetButtonClicked() {
        _state.value = _state.value.copy(showCreatePresetDialog = true)
    }

    fun onPresetHoursChanged(hours: Int) {
        _state.value =
            _state.value.copy(presetDuration = _state.value.presetDuration.copy(hours = hours))
    }

    fun onPresetMinutesChanged(minutes: Int) {
        _state.value =
            _state.value.copy(presetDuration = _state.value.presetDuration.copy(minutes = minutes))
    }

    fun onPresetSecondsChanged(seconds: Int) {
        _state.value =
            _state.value.copy(presetDuration = _state.value.presetDuration.copy(seconds = seconds))
    }

    fun onCreatePresetDialogConfirmed() {
        val presetDuration = _state.value.presetDuration
        val presetInEdit = _state.value.presetInEdit
        _state.value = _state.value.copy(
            showCreatePresetDialog = false,
            presetInEdit = null,
            presetDuration = 0.seconds
        )
        viewModelScope.launch {
            if (presetInEdit != null) {
                repository.editPreset(Preset(presetInEdit.id, presetDuration))
            } else {
                repository.savePreset(presetDuration)
            }
        }
    }

    fun onCreatePresetDialogDismissed() {
        _state.value = _state.value.copy(
            showCreatePresetDialog = false,
            presetInEdit = null,
            presetDuration = 0.seconds
        )
    }

    fun onStartButtonClicked() {
        _state.value = _state.value.copy(navigateToTimerInProgress = true)
    }

    private fun Duration.copy(hours: Int? = null, minutes: Int? = null, seconds: Int? = null) =
        toComponents { prevHours, prevMinutes, prevSeconds, _ ->
            (hours ?: prevHours.toInt()).hours +
                    (minutes ?: prevMinutes).minutes +
                    (seconds ?: prevSeconds).seconds
        }

    data class State(
        val duration: Duration = 0.seconds,
        val presets: List<Preset> = emptyList(),
        val selectedPresetId: UUID? = null,
        val showEditPresetBottomSheet: Boolean = false,
        val presetInEdit: Preset? = null,
        val showCreatePresetDialog: Boolean = false,
        val presetDuration: Duration = 0.seconds,
        val navigateToTimerInProgress: Boolean = false
    )
}
