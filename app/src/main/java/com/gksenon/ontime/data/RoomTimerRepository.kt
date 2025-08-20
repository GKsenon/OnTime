package com.gksenon.ontime.data

import com.gksenon.ontime.domain.Preset
import com.gksenon.ontime.domain.TimerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import kotlin.time.Duration

class RoomTimerRepository(val presetDao: PresetDao) : TimerRepository {

    override suspend fun savePreset(duration: Duration) =
        presetDao.savePreset(PresetEntity(id = UUID.randomUUID(), duration = duration))

    override suspend fun editPreset(preset: Preset) =
        presetDao.editPreset(preset.toEntity())

    override suspend fun deletePreset(preset: Preset) =
        presetDao.deletePreset(preset.toEntity())

    override fun getPresets(): Flow<List<Preset>> = presetDao.getPresets()
        .map { presets -> presets.map { preset -> preset.toPreset() } }

    private fun PresetEntity.toPreset() = Preset(id = id, duration = duration)

    private fun Preset.toEntity() = PresetEntity(id = id, duration = duration)
}
