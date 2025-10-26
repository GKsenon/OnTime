package com.gksenon.ontime.di

import com.gksenon.ontime.domain.Clock
import com.gksenon.ontime.util.SystemClock
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ClockModule {

    @Provides
    fun provideClock(): Clock = SystemClock()
}