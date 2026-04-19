package com.londongemsapp.domain.repository

import com.londongemsapp.domain.model.Category
import com.londongemsapp.domain.model.DataResult
import com.londongemsapp.domain.model.Recommendation
import kotlinx.coroutines.flow.Flow

interface RecommendationRepository {
    /** Returns a reactive stream of recommendations, optionally filtered by [category]. */
    fun getRecommendations(category: Category? = null): Flow<List<Recommendation>>
    /** Returns a reactive stream of a single recommendation by its Reddit ID, or null if not found. */
    fun getRecommendation(redditId: String): Flow<Recommendation?>
    /** Returns a reactive stream of all recommendations marked as favorite. */
    fun getFavorites(): Flow<List<Recommendation>>
    /** Toggles the favorite flag for the recommendation with the given [redditId]. */
    suspend fun toggleFavorite(redditId: String)
    /** Toggles the done flag for the recommendation with the given [redditId]. */
    suspend fun toggleDone(redditId: String)
    /** Clears the favorite flag on all favorited recommendations. */
    suspend fun clearAllFavorites()
    /** Fetches fresh recommendations from Reddit and upserts them into the local database. */
    suspend fun syncFromReddit(): DataResult<Int>
    /** Returns a reactive stream of the last successful sync timestamp in milliseconds, or null if never synced. */
    fun getLastSyncTimestamp(): Flow<Long?>
}
