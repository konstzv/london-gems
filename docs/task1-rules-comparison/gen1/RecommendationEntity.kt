package com.londongems.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recommendations")
data class RecommendationEntity(
    @PrimaryKey
    @ColumnInfo(name = "reddit_id")
    val redditId: String,

    val title: String,

    val body: String,

    val author: String,

    val score: Int,

    val url: String,

    @ColumnInfo(name = "created_utc")
    val createdUtc: Long,

    val subreddit: String,
)
