package com.londongemsapp.domain.repository

import com.londongemsapp.domain.model.Category
import com.londongemsapp.domain.model.DataResult
import com.londongemsapp.domain.model.Recommendation
import kotlinx.coroutines.flow.Flow

interface RecommendationRepository {
    fun getRecommendations(category: Category? = null): Flow<List<Recommendation>>
    fun getRecommendation(redditId: String): Flow<Recommendation?>
    fun getFavorites(): Flow<List<Recommendation>>
    suspend fun toggleFavorite(redditId: String)
    suspend fun toggleDone(redditId: String)
    suspend fun syncFromReddit(): DataResult<Int>
    fun getLastSyncTimestamp(): Flow<Long?>
}
