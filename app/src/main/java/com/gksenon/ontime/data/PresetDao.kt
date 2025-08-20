package com.gksenon.ontime.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PresetDao {

    @Insert
    suspend fun savePreset(preset: PresetEntity)

    @Update
    suspend fun editPreset(preset: PresetEntity)

    @Delete
    suspend fun deletePreset(preset: PresetEntity)

    @Query("SELECT * FROM preset")
    fun getPresets(): Flow<List<PresetEntity>>
}