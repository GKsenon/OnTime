package com.gksenon.ontime.domain

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
interface Clock {
    fun now(): Instant
}