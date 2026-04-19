package com.londongems.data

import com.londongems.data.local.RecommendationDao
import com.londongems.data.local.RecommendationEntity
import com.londongems.data.remote.RedditService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RedditRepository @Inject constructor(
    private val redditService: RedditService,
    private val recommendationDao: RecommendationDao,
) {

    suspend fun fetchFromReddit(): List<RecommendationEntity> {
        val response = redditService.getRecommendations()
        return response.data.children.map { child ->
            val post = child.data
            RecommendationEntity(
                redditId = post.id,
                title = post.title,
                body = post.selftext,
                author = post.author,
                score = post.score,
                url = post.url,
                createdUtc = post.createdUtc.toLong(),
                subreddit = post.subreddit,
            )
        }
    }

    suspend fun upsertAll(recommendations: List<RecommendationEntity>) {
        recommendationDao.upsertAll(recommendations)
    }

    suspend fun getAll(): List<RecommendationEntity> {
        return recommendationDao.getAll()
    }
}
