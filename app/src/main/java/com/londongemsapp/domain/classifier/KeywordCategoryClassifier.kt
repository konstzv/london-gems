package com.londongemsapp.domain.classifier

import com.londongemsapp.domain.model.Category
import com.londongemsapp.domain.model.ClassificationResult
import javax.inject.Inject

class KeywordCategoryClassifier @Inject constructor() : CategoryClassifier {

    private val subredditMapping = mapOf(
        "londonfood" to ClassificationResult(Category.FOOD_AND_DRINKS, 1.0f),
        "londonsocialclub" to ClassificationResult(Category.EVENTS, 1.0f),
        "visitlondon" to ClassificationResult(Category.CULTURE_AND_MUSEUMS, 0.9f),
        "secretlondon" to ClassificationResult(Category.HIDDEN_GEMS, 0.9f)
    )

    private val categoryKeywords = mapOf(
        Category.FOOD_AND_DRINKS to listOf(
            "pub", "restaurant", "cafe", "coffee", "brunch", "dinner", "lunch",
            "food", "eat", "eating", "cocktail", "wine", "beer", "bakery",
            "pizza", "burger", "sushi", "curry", "roast", "breakfast",
            "gastropub", "bistro", "deli", "takeaway", "street food"
        ),
        Category.PARKS_AND_NATURE to listOf(
            "park", "garden", "walk", "nature", "green", "heath", "common",
            "canal", "river", "trail", "woodland", "wildlife", "tree",
            "picnic", "outdoor", "cycling", "running", "hike", "pond",
            "meadow", "botanical", "allotment", "cemetery"
        ),
        Category.CULTURE_AND_MUSEUMS to listOf(
            "museum", "gallery", "exhibition", "theatre", "theater", "art",
            "history", "heritage", "architecture", "library", "cinema",
            "film", "book", "opera", "ballet", "sculpture", "painting",
            "tour", "monument", "cathedral", "church", "palace"
        ),
        Category.NIGHTLIFE to listOf(
            "club", "bar", "nightlife", "dj", "rave", "dancing", "dance",
            "late night", "party", "gig", "live music", "venue", "karaoke",
            "comedy", "standup", "stand-up", "rooftop bar", "speakeasy",
            "cocktail bar", "nightclub", "afterparty"
        ),
        Category.EVENTS to listOf(
            "event", "meetup", "festival", "fair", "market", "workshop",
            "class", "pop-up", "popup", "carnival", "parade", "fireworks",
            "opening", "launch", "tickets", "booking", "weekend",
            "christmas", "halloween", "summer", "winter", "charity"
        ),
        Category.HIDDEN_GEMS to listOf(
            "hidden", "secret", "unknown", "underrated", "overlooked",
            "off the beaten path", "quiet", "tucked away", "lesser known",
            "gem", "spot", "find", "discovered", "stumbled"
        )
    )

    override fun classify(
        subreddit: String,
        title: String,
        body: String,
        flair: String?
    ): ClassificationResult {
        val normalizedSubreddit = subreddit.lowercase().removePrefix("r/")

        subredditMapping[normalizedSubreddit]?.let { return it }

        return classifyByKeywords(title, body, flair)
    }

    private fun classifyByKeywords(
        title: String,
        body: String,
        flair: String?
    ): ClassificationResult {
        val searchText = buildString {
            append(title.lowercase())
            append(" ")
            append(body.lowercase())
            flair?.let {
                append(" ")
                append(it.lowercase())
            }
        }

        val scores = categoryKeywords.mapValues { (_, keywords) ->
            val matchCount = keywords.count { keyword -> keyword in searchText }
            if (keywords.isNotEmpty()) matchCount.toFloat() / keywords.size else 0f
        }

        val bestMatch = scores.maxByOrNull { it.value }

        if (bestMatch == null || bestMatch.value < 0.01f) {
            return ClassificationResult(Category.UNCATEGORIZED, 0f)
        }

        val confidence = (bestMatch.value * 10f).coerceAtMost(1.0f)

        if (confidence < 0.3f) {
            return ClassificationResult(Category.UNCATEGORIZED, confidence)
        }

        return ClassificationResult(bestMatch.key, confidence)
    }
}
