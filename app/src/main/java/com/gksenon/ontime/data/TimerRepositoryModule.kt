package com.gksenon.ontime.data

import android.content.Context
import androidx.room.Room
import com.gksenon.ontime.domain.TimerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class TimerRepositoryModule {

    @Provides
    @Singleton
    fun provideTimerDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, RoomTimerDatabase::class.java, "timer").build()

    @Provides
    @Singleton
    fun providePresetDao(database: RoomTimerDatabase) = database.getPresetDao()

    @Provides
    @Singleton
    fun provideTimerRepository(presetDao: PresetDao): TimerRepository =
        RoomTimerRepository(presetDao)
}