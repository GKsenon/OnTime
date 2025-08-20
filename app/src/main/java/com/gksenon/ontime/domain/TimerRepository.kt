package com.gksenon.ontime.domain

import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface TimerRepository {

    suspend fun savePreset(duration: Duration)

    suspend fun editPreset(preset: Preset)

    suspend fun deletePreset(preset: Preset)

    fun getPresets(): Flow<List<Preset>>
}
