package com.gksenon.ontime.di

import com.gksenon.ontime.domain.Clock
import com.gksenon.ontime.domain.FlagsRepository
import com.gksenon.ontime.domain.TimerRepository
import com.gksenon.ontime.viewmodel.TimerInProgressViewModel
import com.gksenon.ontime.viewmodel.TimerInitViewModel
import com.gksenon.ontime.viewmodel.TimerRingingViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
class ViewModelModule {

    @Provides
    fun provideTimerInitViewModel(timerRepository: TimerRepository) = TimerInitViewModel(timerRepository)

    @Provides
    fun provideTimerInProgressViewModel(flagsRepository: FlagsRepository) =
        TimerInProgressViewModel(flagsRepository)

    @Provides
    fun provideTimerRingingViewModel(clock: Clock) = TimerRingingViewModel(clock)
}