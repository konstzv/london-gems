package com.londongemsapp.domain.classifier

import com.londongemsapp.domain.model.Category
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class KeywordCategoryClassifierTest {

    private lateinit var classifier: KeywordCategoryClassifier

    @Before
    fun setUp() {
        classifier = KeywordCategoryClassifier()
    }

    // -- Subreddit-based classification --

    @Test
    fun classify_londonfoodSubreddit_returnsFoodWithFullConfidence() {
        val result = classifier.classify(
            subreddit = "londonfood",
            title = "anything",
            body = "anything",
            flair = null,
        )

        assertEquals(Category.FOOD_AND_DRINKS, result.category)
        assertEquals(1.0f, result.confidence, 0.001f)
    }

    @Test
    fun classify_londonsocialclubSubreddit_returnsEventsWithFullConfidence() {
        val result = classifier.classify(
            subreddit = "LondonSocialClub",
            title = "anything",
            body = "anything",
            flair = null,
        )

        assertEquals(Category.EVENTS, result.category)
        assertEquals(1.0f, result.confidence, 0.001f)
    }

    @Test
    fun classify_visitlondonSubreddit_returnsCultureWithHighConfidence() {
        val result = classifier.classify(
            subreddit = "visitlondon",
            title = "anything",
            body = "anything",
            flair = null,
        )

        assertEquals(Category.CULTURE_AND_MUSEUMS, result.category)
        assertEquals(0.9f, result.confidence, 0.001f)
    }

    @Test
    fun classify_secretlondonSubreddit_returnsHiddenGemsWithHighConfidence() {
        val result = classifier.classify(
            subreddit = "SecretLondon",
            title = "anything",
            body = "anything",
            flair = null,
        )

        assertEquals(Category.HIDDEN_GEMS, result.category)
        assertEquals(0.9f, result.confidence, 0.001f)
    }

    @Test
    fun classify_subredditWithRSlashPrefix_stripsPrefix() {
        val result = classifier.classify(
            subreddit = "r/londonfood",
            title = "anything",
            body = "anything",
            flair = null,
        )

        assertEquals(Category.FOOD_AND_DRINKS, result.category)
    }

    @Test
    fun classify_subredditCaseInsensitive_matchesRegardlessOfCase() {
        val result = classifier.classify(
            subreddit = "LONDONFOOD",
            title = "anything",
            body = "anything",
            flair = null,
        )

        assertEquals(Category.FOOD_AND_DRINKS, result.category)
    }

    // -- Keyword-based classification for generic subreddits --

    @Test
    fun classify_londonSubredditWithFoodKeywords_returnsFood() {
        val result = classifier.classify(
            subreddit = "london",
            title = "Best pub for Sunday roast near Shoreditch?",
            body = "Looking for a great gastropub with good beer and a proper roast dinner",
            flair = null,
        )

        assertEquals(Category.FOOD_AND_DRINKS, result.category)
    }

    @Test
    fun classify_londonSubredditWithParkKeywords_returnsParks() {
        val result = classifier.classify(
            subreddit = "london",
            title = "Lovely walk through the park today",
            body = "Found a beautiful trail near the canal with great nature and a pond",
            flair = null,
        )

        assertEquals(Category.PARKS_AND_NATURE, result.category)
    }

    @Test
    fun classify_londonSubredditWithCultureKeywords_returnsCulture() {
        val result = classifier.classify(
            subreddit = "london",
            title = "New exhibition at the museum",
            body = "Amazing gallery and art exhibition about heritage architecture",
            flair = null,
        )

        assertEquals(Category.CULTURE_AND_MUSEUMS, result.category)
    }

    @Test
    fun classify_londonSubredditWithNightlifeKeywords_returnsNightlife() {
        val result = classifier.classify(
            subreddit = "london",
            title = "Best bar for dancing?",
            body = "Looking for a club with a DJ and live music venue",
            flair = null,
        )

        assertEquals(Category.NIGHTLIFE, result.category)
    }

    @Test
    fun classify_londonSubredditWithEventKeywords_returnsEvents() {
        val result = classifier.classify(
            subreddit = "london",
            title = "Weekend festival coming up",
            body = "Free market and workshop event with pop-up food stalls and tickets available",
            flair = null,
        )

        assertEquals(Category.EVENTS, result.category)
    }

    @Test
    fun classify_londonSubredditWithHiddenGemKeywords_returnsHiddenGems() {
        val result = classifier.classify(
            subreddit = "london",
            title = "Found this hidden secret spot",
            body = "Stumbled upon an underrated gem tucked away from tourists",
            flair = null,
        )

        assertEquals(Category.HIDDEN_GEMS, result.category)
    }

    @Test
    fun classify_flairContributesToClassification_usesFlairText() {
        val result = classifier.classify(
            subreddit = "london",
            title = "Any suggestions?",
            body = "Looking for somewhere nice to go for a pub lunch with good beer",
            flair = "Food & Drink restaurant cafe",
        )

        assertEquals(Category.FOOD_AND_DRINKS, result.category)
    }

    // -- Confidence threshold --

    @Test
    fun classify_belowConfidenceThreshold_returnsUncategorized() {
        // A single keyword match out of ~25 yields confidence < 0.3
        val result = classifier.classify(
            subreddit = "london",
            title = "Question about transport in the city",
            body = "How do I get the tube to Heathrow? I have a green card.",
            flair = null,
        )

        assertEquals(Category.UNCATEGORIZED, result.category)
    }

    // -- Edge cases --

    @Test
    fun classify_emptyTitleAndBody_returnsUncategorized() {
        val result = classifier.classify(
            subreddit = "london",
            title = "",
            body = "",
            flair = null,
        )

        assertEquals(Category.UNCATEGORIZED, result.category)
        assertEquals(0f, result.confidence, 0.001f)
    }

    @Test
    fun classify_noKeywordsMatch_returnsUncategorizedWithZeroConfidence() {
        val result = classifier.classify(
            subreddit = "london",
            title = "Plumber needed in Croydon",
            body = "My boiler is broken and I need someone who can fix it ASAP",
            flair = null,
        )

        assertEquals(Category.UNCATEGORIZED, result.category)
    }

    @Test
    fun classify_subredditMappingTakesPriorityOverKeywords_ignoresBodyKeywords() {
        val result = classifier.classify(
            subreddit = "londonfood",
            title = "Best club for dancing tonight",
            body = "Looking for a club bar nightlife DJ rave",
            flair = null,
        )

        // Subreddit mapping wins even though body is full of nightlife keywords
        assertEquals(Category.FOOD_AND_DRINKS, result.category)
        assertEquals(1.0f, result.confidence, 0.001f)
    }

    @Test
    fun classify_nullFlair_doesNotThrow() {
        val result = classifier.classify(
            subreddit = "london",
            title = "Best pub in town",
            body = "Great beer and roast here at this restaurant",
            flair = null,
        )

        assertEquals(Category.FOOD_AND_DRINKS, result.category)
    }

    @Test
    fun classify_confidenceIsCappedAtOne() {
        // Even with many keyword matches, confidence should not exceed 1.0
        val result = classifier.classify(
            subreddit = "london",
            title = "pub restaurant cafe coffee brunch dinner lunch food",
            body = "eat eating cocktail wine beer bakery pizza burger sushi curry roast breakfast gastropub bistro deli takeaway street food",
            flair = null,
        )

        assertTrue(result.confidence <= 1.0f)
    }

    @Test
    fun classify_unknownSubreddit_fallsBackToKeywords() {
        val result = classifier.classify(
            subreddit = "someRandomSubreddit",
            title = "Beautiful park and garden walk",
            body = "Found a lovely trail through the woodland with wildlife and a pond",
            flair = null,
        )

        assertEquals(Category.PARKS_AND_NATURE, result.category)
    }
}
