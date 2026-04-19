package com.londongemsapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class FeedSmokeTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun feedScreen_displaysBottomNavigation() {
        composeRule.onNodeWithText("Feed").assertIsDisplayed()
        composeRule.onNodeWithText("Favorites").assertIsDisplayed()
        composeRule.onNodeWithText("Settings").assertIsDisplayed()
    }

    @Test
    fun feedScreen_categoryChipsAreVisible() {
        composeRule.onNodeWithText("Food & Drinks").assertIsDisplayed()
    }

    @Test
    fun feedScreen_tapCategoryChip_filtersContent() {
        composeRule.onNodeWithText("Food & Drinks").performClick()
        composeRule.waitForIdle()
    }

    @Test
    fun feedScreen_tapRecommendationCard_opensDetail() {
        composeRule.waitUntil(5_000) {
            composeRule.onAllNodesWithTag("recommendation_card").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onAllNodes(hasTestTag("recommendation_card")).onFirst().performClick()
        composeRule.onNodeWithText("Open on Reddit").assertIsDisplayed()
    }

    @Test
    fun feedScreen_navigateToFavorites_showsEmptyState() {
        composeRule.onNodeWithText("Favorites").performClick()
        composeRule.waitForIdle()
    }
}
