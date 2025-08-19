package com.gksenon.ontime.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID
import kotlin.time.Duration

@Entity(tableName = "preset")
data class PresetEntity(@PrimaryKey val id: UUID, val duration: Duration)
