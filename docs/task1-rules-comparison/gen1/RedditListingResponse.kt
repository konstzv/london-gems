package com.londongems.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RedditListingResponse(
    val data: ListingData,
)

@JsonClass(generateAdapter = true)
data class ListingData(
    val children: List<PostWrapper>,
)

@JsonClass(generateAdapter = true)
data class PostWrapper(
    val data: PostData,
)

@JsonClass(generateAdapter = true)
data class PostData(
    val id: String,
    val title: String,
    val selftext: String,
    val author: String,
    val score: Int,
    val url: String,
    @Json(name = "created_utc") val createdUtc: Double,
    val subreddit: String,
)
