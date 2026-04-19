package com.londongemsapp.data.remote

import com.londongemsapp.data.remote.dto.RedditPostDto
import com.londongemsapp.domain.classifier.CategoryClassifier
import com.londongemsapp.domain.model.Category
import com.londongemsapp.domain.model.Recommendation
import javax.inject.Inject

class RedditDtoMapper @Inject constructor(
    private val classifier: CategoryClassifier,
) {

    fun mapToRecommendations(posts: List<RedditPostDto>): List<Recommendation> =
        posts.mapNotNull { dto -> mapSingle(dto) }

    private fun mapSingle(dto: RedditPostDto): Recommendation? {
        if (isDeletedOrRemoved(dto)) return null

        val body = dto.selftext.orEmpty()
        if (body.isBlank()) return null

        val classification = classifier.classify(
            subreddit = dto.subreddit,
            title = dto.title,
            body = body,
            flair = dto.linkFlairText,
        )

        return Recommendation(
            redditId = dto.id,
            title = dto.title,
            body = body,
            subreddit = dto.subreddit,
            category = classification.category,
            score = dto.score,
            url = REDDIT_BASE_URL + dto.permalink,
            thumbnailUrl = dto.thumbnail.takeIf { it.isValidThumbnailUrl() },
            createdAt = (dto.createdUtc * 1000).toLong(),
        )
    }

    private fun isDeletedOrRemoved(dto: RedditPostDto): Boolean {
        val selftext = dto.selftext.orEmpty()
        return selftext == DELETED_MARKER ||
            selftext == REMOVED_MARKER ||
            dto.removedByCategory != null
    }

    private fun String?.isValidThumbnailUrl(): Boolean =
        this != null && (startsWith("http://") || startsWith("https://"))

    companion object {
        private const val REDDIT_BASE_URL = "https://www.reddit.com"
        private const val DELETED_MARKER = "[deleted]"
        private const val REMOVED_MARKER = "[removed]"
    }
}
