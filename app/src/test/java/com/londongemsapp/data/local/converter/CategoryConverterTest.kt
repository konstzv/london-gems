package com.londongemsapp.data.local.converter

import com.londongemsapp.domain.model.Category
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CategoryConverterTest {

    private lateinit var converter: CategoryConverter

    @Before
    fun setUp() {
        converter = CategoryConverter()
    }

    @Test
    fun `fromCategory converts FOOD_AND_DRINKS to string`() {
        assertEquals("FOOD_AND_DRINKS", converter.fromCategory(Category.FOOD_AND_DRINKS))
    }

    @Test
    fun `fromCategory converts EVENTS to string`() {
        assertEquals("EVENTS", converter.fromCategory(Category.EVENTS))
    }

    @Test
    fun `fromCategory converts PARKS_AND_NATURE to string`() {
        assertEquals("PARKS_AND_NATURE", converter.fromCategory(Category.PARKS_AND_NATURE))
    }

    @Test
    fun `fromCategory converts CULTURE_AND_MUSEUMS to string`() {
        assertEquals("CULTURE_AND_MUSEUMS", converter.fromCategory(Category.CULTURE_AND_MUSEUMS))
    }

    @Test
    fun `fromCategory converts NIGHTLIFE to string`() {
        assertEquals("NIGHTLIFE", converter.fromCategory(Category.NIGHTLIFE))
    }

    @Test
    fun `fromCategory converts HIDDEN_GEMS to string`() {
        assertEquals("HIDDEN_GEMS", converter.fromCategory(Category.HIDDEN_GEMS))
    }

    @Test
    fun `fromCategory converts UNCATEGORIZED to string`() {
        assertEquals("UNCATEGORIZED", converter.fromCategory(Category.UNCATEGORIZED))
    }

    @Test
    fun `toCategory converts string to FOOD_AND_DRINKS`() {
        assertEquals(Category.FOOD_AND_DRINKS, converter.toCategory("FOOD_AND_DRINKS"))
    }

    @Test
    fun `toCategory converts string to EVENTS`() {
        assertEquals(Category.EVENTS, converter.toCategory("EVENTS"))
    }

    @Test
    fun `toCategory converts string to PARKS_AND_NATURE`() {
        assertEquals(Category.PARKS_AND_NATURE, converter.toCategory("PARKS_AND_NATURE"))
    }

    @Test
    fun `toCategory converts string to CULTURE_AND_MUSEUMS`() {
        assertEquals(Category.CULTURE_AND_MUSEUMS, converter.toCategory("CULTURE_AND_MUSEUMS"))
    }

    @Test
    fun `toCategory converts string to NIGHTLIFE`() {
        assertEquals(Category.NIGHTLIFE, converter.toCategory("NIGHTLIFE"))
    }

    @Test
    fun `toCategory converts string to HIDDEN_GEMS`() {
        assertEquals(Category.HIDDEN_GEMS, converter.toCategory("HIDDEN_GEMS"))
    }

    @Test
    fun `toCategory converts string to UNCATEGORIZED`() {
        assertEquals(Category.UNCATEGORIZED, converter.toCategory("UNCATEGORIZED"))
    }

    @Test
    fun `roundtrip conversion preserves all categories`() {
        Category.entries.forEach { category ->
            val converted = converter.fromCategory(category)
            val restored = converter.toCategory(converted)
            assertEquals(category, restored)
        }
    }
}
