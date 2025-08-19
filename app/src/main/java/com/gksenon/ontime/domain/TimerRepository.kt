package com.gksenon.ontime.domain

import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface TimerRepository {

    suspend fun savePreset(duration: Duration)

    fun getPresets(): Flow<List<Preset>>
}
