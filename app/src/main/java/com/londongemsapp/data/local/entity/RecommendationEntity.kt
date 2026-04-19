package com.londongemsapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.londongemsapp.domain.model.Category

@Entity(tableName = "recommendations")
data class RecommendationEntity(
    @PrimaryKey val redditId: String,
    val title: String,
    val body: String,
    val subreddit: String,
    val category: Category,
    val score: Int,
    val url: String,
    val thumbnailUrl: String?,
    val isFavorite: Boolean = false,
    val isDone: Boolean = false,
    val createdAt: Long,
    val fetchedAt: Long,
)
