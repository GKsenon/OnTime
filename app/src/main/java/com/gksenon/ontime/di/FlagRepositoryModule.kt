package com.gksenon.ontime.di

import com.gksenon.ontime.domain.FlagsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FlagRepositoryModule {

    @Provides
    @Singleton
    fun provideFlagsRepository() = FlagsRepository()
}