package com.gksenon.ontime.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PresetDao {

    @Insert
    suspend fun savePreset(preset: PresetEntity)

    @Query("SELECT * FROM preset")
    fun getPresets(): Flow<List<PresetEntity>>
}