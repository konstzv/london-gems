package com.londongemsapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class RedditListingResponse(
    val kind: String,
    val data: RedditListingData,
)

@Serializable
data class RedditListingData(
    val children: List<RedditChild>,
    val after: String? = null,
)

@Serializable
data class RedditChild(
    val kind: String,
    val data: RedditPostDto,
)

@Serializable
data class RedditPostDto(
    val id: String,
    val title: String,
    val selftext: String? = null,
    val subreddit: String,
    val score: Int,
    val permalink: String,
    val thumbnail: String? = null,
    val preview: RedditPreview? = null,
    @SerialName("created_utc")
    val createdUtc: Double,
    @SerialName("link_flair_text")
    val linkFlairText: String? = null,
    @SerialName("removed_by_category")
    val removedByCategory: String? = null,
    val edited: JsonElement? = null,
)

@Serializable
data class RedditPreview(
    val images: List<RedditPreviewImage> = emptyList(),
)

@Serializable
data class RedditPreviewImage(
    val source: RedditImageSource,
)

@Serializable
data class RedditImageSource(
    val url: String,
    val width: Int,
    val height: Int,
)
