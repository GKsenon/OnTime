package com.gksenon.ontime.data

import androidx.room.TypeConverter
import java.util.UUID

class UUIDConverter {

    @TypeConverter
    fun fromStringToUUID(id: String) = UUID.fromString(id)

    @TypeConverter
    fun fromUUIDToString(id: UUID) = id.toString()
}