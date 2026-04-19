package com.londongemsapp.di

import android.content.Context
import androidx.room.Room
import com.londongemsapp.data.local.AppDatabase
import com.londongemsapp.data.local.RecommendationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "london_gems.db"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideRecommendationDao(database: AppDatabase): RecommendationDao =
        database.recommendationDao()
}
