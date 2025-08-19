package com.gksenon.ontime.data

import androidx.room.TypeConverter
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class DurationConverter {

    @TypeConverter
    fun fromSecondsToDuration(seconds: Long) = seconds.seconds

    @TypeConverter
    fun fromDurationToSeconds(duration: Duration) = duration.inWholeSeconds
}