package com.londongemsapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.londongemsapp.data.local.converter.CategoryConverter
import com.londongemsapp.data.local.entity.RecommendationEntity

@Database(
    entities = [RecommendationEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(CategoryConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recommendationDao(): RecommendationDao
}
