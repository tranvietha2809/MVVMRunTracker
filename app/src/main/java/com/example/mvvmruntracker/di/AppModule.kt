package com.example.mvvmruntracker.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.room.Room
import com.example.mvvmruntracker.db.RunningDatabase
import com.example.mvvmruntracker.other.Constants
import com.example.mvvmruntracker.other.SharedPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRunningDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        RunningDatabase::class.java,
        Constants.RUNNING_DATABASE_NAME
    ).build()

    @Provides
    @Singleton
    fun provideDao(db: RunningDatabase) = db.getRunDao()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app: Context) =
        app.getSharedPreferences(SharedPrefs.SHARED_PREFERENCES_NAME, MODE_PRIVATE)
}