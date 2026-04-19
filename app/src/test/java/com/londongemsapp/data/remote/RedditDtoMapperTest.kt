package com.londongemsapp.data.remote

import com.londongemsapp.data.remote.dto.RedditPostDto
import com.londongemsapp.domain.classifier.CategoryClassifier
import com.londongemsapp.domain.model.Category
import com.londongemsapp.domain.model.ClassificationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RedditDtoMapperTest {

    private lateinit var mapper: RedditDtoMapper

    /**
     * Stub classifier that always returns a fixed category.
     * Tests for RedditDtoMapper should not depend on real classification logic.
     */
    private class StubClassifier(
        private val result: ClassificationResult = ClassificationResult(Category.FOOD_AND_DRINKS, 0.9f),
    ) : CategoryClassifier {
        override fun classify(
            subreddit: String,
            title: String,
            body: String,
            flair: String?,
        ): ClassificationResult = result
    }

    @Before
    fun setUp() {
        mapper = RedditDtoMapper(StubClassifier())
    }

    // -- Filtering deleted/removed posts --

    @Test
    fun mapToRecommendations_deletedPost_filteredOut() {
        val posts = listOf(
            makeDto(selftext = "[deleted]"),
        )

        val result = mapper.mapToRecommendations(posts)

        assertTrue(result.isEmpty())
    }

    @Test
    fun mapToRecommendations_removedPost_filteredOut() {
        val posts = listOf(
            makeDto(selftext = "[removed]"),
        )

        val result = mapper.mapToRecommendations(posts)

        assertTrue(result.isEmpty())
    }

    @Test
    fun mapToRecommendations_removedByCategory_filteredOut() {
        val posts = listOf(
            makeDto(
                selftext = "Some content",
                removedByCategory = "moderator",
            ),
        )

        val result = mapper.mapToRecommendations(posts)

        assertTrue(result.isEmpty())
    }

    // -- Empty body filtering --

    @Test
    fun mapToRecommendations_nullSelftext_filteredOut() {
        val posts = listOf(
            makeDto(selftext = null),
        )

        val result = mapper.mapToRecommendations(posts)

        assertTrue(result.isEmpty())
    }

    @Test
    fun mapToRecommendations_emptySelftext_filteredOut() {
        val posts = listOf(
            makeDto(selftext = ""),
        )

        val result = mapper.mapToRecommendations(posts)

        assertTrue(result.isEmpty())
    }

    @Test
    fun mapToRecommendations_blankSelftext_filteredOut() {
        val posts = listOf(
            makeDto(selftext = "   "),
        )

        val result = mapper.mapToRecommendations(posts)

        assertTrue(result.isEmpty())
    }

    // -- URL construction --

    @Test
    fun mapToRecommendations_validPost_constructsFullRedditUrl() {
        val posts = listOf(
            makeDto(permalink = "/r/london/comments/abc123/great_post/"),
        )

        val result = mapper.mapToRecommendations(posts)

        assertEquals(
            "https://www.reddit.com/r/london/comments/abc123/great_post/",
            result.first().url,
        )
    }

    // -- Thumbnail validation --

    @Test
    fun mapToRecommendations_httpsThumbnail_preserved() {
        val posts = listOf(
            makeDto(thumbnail = "https://i.redd.it/abc123.jpg"),
        )

        val result = mapper.mapToRecommendations(posts)

        assertEquals("https://i.redd.it/abc123.jpg", result.first().thumbnailUrl)
    }

    @Test
    fun mapToRecommendations_httpThumbnail_preserved() {
        val posts = listOf(
            makeDto(thumbnail = "http://i.redd.it/abc123.jpg"),
        )

        val result = mapper.mapToRecommendations(posts)

        assertEquals("http://i.redd.it/abc123.jpg", result.first().thumbnailUrl)
    }

    @Test
    fun mapToRecommendations_selfThumbnailPlaceholder_setToNull() {
        val posts = listOf(
            makeDto(thumbnail = "self"),
        )

        val result = mapper.mapToRecommendations(posts)

        assertNull(result.first().thumbnailUrl)
    }

    @Test
    fun mapToRecommendations_defaultThumbnailPlaceholder_setToNull() {
        val posts = listOf(
            makeDto(thumbnail = "default"),
        )

        val result = mapper.mapToRecommendations(posts)

        assertNull(result.first().thumbnailUrl)
    }

    @Test
    fun mapToRecommendations_nullThumbnail_remainsNull() {
        val posts = listOf(
            makeDto(thumbnail = null),
        )

        val result = mapper.mapToRecommendations(posts)

        assertNull(result.first().thumbnailUrl)
    }

    // -- Field mapping --

    @Test
    fun mapToRecommendations_validPost_mapsAllFieldsCorrectly() {
        val dto = makeDto(
            id = "post_42",
            title = "Best cafe in Shoreditch",
            selftext = "Found this amazing little spot",
            subreddit = "london",
            score = 150,
            permalink = "/r/london/comments/post_42/best_cafe/",
            thumbnail = "https://i.redd.it/thumb.jpg",
            createdUtc = 1700000000.0,
            linkFlairText = "Food",
        )

        val result = mapper.mapToRecommendations(listOf(dto))

        assertEquals(1, result.size)
        val recommendation = result.first()
        assertEquals("post_42", recommendation.redditId)
        assertEquals("Best cafe in Shoreditch", recommendation.title)
        assertEquals("Found this amazing little spot", recommendation.body)
        assertEquals("london", recommendation.subreddit)
        assertEquals(Category.FOOD_AND_DRINKS, recommendation.category)
        assertEquals(150, recommendation.score)
        assertEquals(
            "https://www.reddit.com/r/london/comments/post_42/best_cafe/",
            recommendation.url,
        )
        assertEquals("https://i.redd.it/thumb.jpg", recommendation.thumbnailUrl)
        assertEquals(1700000000000L, recommendation.createdAt)
    }

    @Test
    fun mapToRecommendations_createdUtcTimestamp_convertedToMilliseconds() {
        val posts = listOf(
            makeDto(createdUtc = 1234567890.0),
        )

        val result = mapper.mapToRecommendations(posts)

        assertEquals(1234567890000L, result.first().createdAt)
    }

    @Test
    fun mapToRecommendations_usesClassifierCategory_notHardcoded() {
        val parksClassifier = StubClassifier(
            ClassificationResult(Category.PARKS_AND_NATURE, 0.8f),
        )
        val parksMapper = RedditDtoMapper(parksClassifier)

        val posts = listOf(makeDto())

        val result = parksMapper.mapToRecommendations(posts)

        assertEquals(Category.PARKS_AND_NATURE, result.first().category)
    }

    // -- Mixed list handling --

    @Test
    fun mapToRecommendations_mixedValidAndInvalid_returnsOnlyValid() {
        val posts = listOf(
            makeDto(id = "valid_1", selftext = "Good content"),
            makeDto(id = "deleted", selftext = "[deleted]"),
            makeDto(id = "valid_2", selftext = "More content"),
            makeDto(id = "removed", selftext = "[removed]"),
            makeDto(id = "empty", selftext = ""),
            makeDto(id = "valid_3", selftext = "Final content"),
        )

        val result = mapper.mapToRecommendations(posts)

        assertEquals(3, result.size)
        assertEquals(listOf("valid_1", "valid_2", "valid_3"), result.map { it.redditId })
    }

    @Test
    fun mapToRecommendations_emptyList_returnsEmptyList() {
        val result = mapper.mapToRecommendations(emptyList())

        assertTrue(result.isEmpty())
    }

    @Test
    fun mapToRecommendations_defaultIsFavoriteAndIsDone_areFalse() {
        val posts = listOf(makeDto())

        val result = mapper.mapToRecommendations(posts)

        val recommendation = result.first()
        assertNotNull(recommendation)
        assertEquals(false, recommendation.isFavorite)
        assertEquals(false, recommendation.isDone)
    }

    // -- Helper --

    private fun makeDto(
        id: String = "test_id",
        title: String = "Test Title",
        selftext: String? = "Test body content",
        subreddit: String = "london",
        score: Int = 42,
        permalink: String = "/r/london/comments/test_id/test_post/",
        thumbnail: String? = null,
        createdUtc: Double = 1700000000.0,
        linkFlairText: String? = null,
        removedByCategory: String? = null,
    ) = RedditPostDto(
        id = id,
        title = title,
        selftext = selftext,
        subreddit = subreddit,
        score = score,
        permalink = permalink,
        thumbnail = thumbnail,
        createdUtc = createdUtc,
        linkFlairText = linkFlairText,
        removedByCategory = removedByCategory,
    )
}
