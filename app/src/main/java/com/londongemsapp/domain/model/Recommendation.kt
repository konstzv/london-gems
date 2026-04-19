package com.londongemsapp.domain.model

data class Recommendation(
    val redditId: String,
    val title: String,
    val body: String,
    val subreddit: String,
    val category: Category,
    val score: Int,
    val url: String,
    val thumbnailUrl: String?,
    val isFavorite: Boolean = false,
    val isDone: Boolean = false,
    val createdAt: Long
)
