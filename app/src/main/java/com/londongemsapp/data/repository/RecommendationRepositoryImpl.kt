package com.londongemsapp.data.repository

import com.londongemsapp.data.local.RecommendationDao
import com.londongemsapp.data.local.SyncPreferences
import com.londongemsapp.data.remote.RedditApi
import com.londongemsapp.data.remote.RedditDtoMapper
import com.londongemsapp.domain.model.Category
import com.londongemsapp.domain.model.DataResult
import com.londongemsapp.domain.model.Recommendation
import com.londongemsapp.domain.repository.RecommendationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecommendationRepositoryImpl @Inject constructor(
    private val redditApi: RedditApi,
    private val dao: RecommendationDao,
    private val mapper: RedditDtoMapper,
    private val syncPreferences: SyncPreferences,
) : RecommendationRepository {

    override fun getRecommendations(category: Category?): Flow<List<Recommendation>> {
        val entityFlow = if (category != null) {
            dao.getByCategory(category)
        } else {
            dao.getAll()
        }
        return entityFlow.map { entities -> entities.map { it.toDomain() } }
    }

    override fun getRecommendation(redditId: String): Flow<Recommendation?> =
        dao.getById(redditId).map { it?.toDomain() }

    override fun getFavorites(): Flow<List<Recommendation>> =
        dao.getFavorites().map { entities -> entities.map { it.toDomain() } }

    override suspend fun toggleFavorite(redditId: String) {
        dao.toggleFavorite(redditId)
    }

    override suspend fun toggleDone(redditId: String) {
        dao.toggleDone(redditId)
    }

    override suspend fun clearAllFavorites() {
        dao.clearAllFavorites()
    }

    override suspend fun syncFromReddit(): DataResult<Int> =
        try {
            val now = System.currentTimeMillis()
            val allPosts = buildList {
                addAll(fetchSubreddit("london"))
                addAll(fetchSubreddit("LondonSocialClub"))
            }
            val recommendations = mapper.mapToRecommendations(allPosts)
            val entities = recommendations.map { it.toEntity(fetchedAt = now) }
            dao.upsertFromNetwork(entities)

            syncPreferences.setLastSyncTimestamp(now)

            DataResult.Success(entities.size)
        } catch (e: Exception) {
            DataResult.Error(e)
        }

    override fun getLastSyncTimestamp(): Flow<Long?> = syncPreferences.getLastSyncTimestamp()

    private suspend fun fetchSubreddit(subreddit: String) =
        try {
            val hot = redditApi.getHotPosts(subreddit)
            val top = redditApi.getTopPosts(subreddit)
            val allChildren = hot.data.children + top.data.children
            // Deduplicate by post id across hot and top
            allChildren.distinctBy { it.data.id }.map { it.data }
        } catch (e: Exception) {
            // If a single subreddit fails, skip it and continue with others
            emptyList()
        }

}
