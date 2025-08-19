package com.gksenon.ontime.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(version = 1, entities = [PresetEntity::class])
@TypeConverters(UUIDConverter::class, DurationConverter::class)
abstract class RoomTimerDatabase: RoomDatabase() {
    abstract fun getPresetDao(): PresetDao
}