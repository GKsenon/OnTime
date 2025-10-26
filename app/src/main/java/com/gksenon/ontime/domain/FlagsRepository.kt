package com.gksenon.ontime.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date
import kotlin.time.Duration.Companion.milliseconds

class FlagsRepository {
    private val _flags = MutableStateFlow<List<Flag>>(emptyList())
    val flags = _flags.asStateFlow()

    private var startTimeStamp: Long = 0L

    fun start() {
        startTimeStamp = Date().time
        _flags.value = emptyList()
    }

    fun saveFlag() {
        val id = _flags.value.lastOrNull()?.id?.plus(1) ?: 1
        val duration = (Date().time - startTimeStamp).milliseconds
        _flags.value = _flags.value.plus(Flag(id, duration))
    }
}