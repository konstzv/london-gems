package com.londongemsapp.data.repository

import com.londongemsapp.data.local.entity.RecommendationEntity
import com.londongemsapp.domain.model.Recommendation

internal fun RecommendationEntity.toDomain() = Recommendation(
    redditId = redditId,
    title = title,
    body = body,
    subreddit = subreddit,
    category = category,
    score = score,
    url = url,
    thumbnailUrl = thumbnailUrl,
    isFavorite = isFavorite,
    isDone = isDone,
    createdAt = createdAt,
)

internal fun Recommendation.toEntity(fetchedAt: Long) = RecommendationEntity(
    redditId = redditId,
    title = title,
    body = body,
    subreddit = subreddit,
    category = category,
    score = score,
    url = url,
    thumbnailUrl = thumbnailUrl,
    isFavorite = isFavorite,
    isDone = isDone,
    createdAt = createdAt,
    fetchedAt = fetchedAt,
)
