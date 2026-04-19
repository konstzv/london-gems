package com.londongems.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface RecommendationDao {

    @Upsert
    suspend fun upsertAll(recommendations: List<RecommendationEntity>)

    @Query("SELECT * FROM recommendations ORDER BY score DESC")
    suspend fun getAll(): List<RecommendationEntity>

    @Query("DELETE FROM recommendations")
    suspend fun deleteAll()
}
