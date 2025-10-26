package com.gksenon.ontime

import com.gksenon.ontime.domain.Clock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class, ExperimentalCoroutinesApi::class)
class TestClock(private val testCoroutineScheduler: TestCoroutineScheduler): Clock {
    override fun now(): Instant =
        Instant.fromEpochMilliseconds(testCoroutineScheduler.currentTime)
}