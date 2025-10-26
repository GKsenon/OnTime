package com.gksenon.ontime.util

import com.gksenon.ontime.domain.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class SystemClock : Clock {

    override fun now(): Instant = kotlin.time.Clock.System.now()
}