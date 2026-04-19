package com.londongemsapp.domain.usecase

import com.londongemsapp.domain.model.Category
import com.londongemsapp.domain.model.DataResult
import com.londongemsapp.domain.model.Recommendation
import com.londongemsapp.domain.repository.RecommendationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ToggleFavoriteUseCaseTest {

    private val fakeRepository = FakeRepository()
    private val useCase = ToggleFavoriteUseCase(fakeRepository)

    @Test
    fun `invoke delegates to repository toggleFavorite`() = runBlocking {
        useCase("post_123")

        assertTrue(fakeRepository.toggleFavoriteCalls.isNotEmpty())
        assertEquals("post_123", fakeRepository.toggleFavoriteCalls.first())
    }

    @Test
    fun `invoke passes correct redditId`() = runBlocking {
        useCase("abc")
        useCase("xyz")

        assertEquals(listOf("abc", "xyz"), fakeRepository.toggleFavoriteCalls)
    }

    private class FakeRepository : RecommendationRepository {
        val toggleFavoriteCalls = mutableListOf<String>()

        override fun getRecommendations(category: Category?): Flow<List<Recommendation>> = emptyFlow()
        override fun getRecommendation(redditId: String): Flow<Recommendation?> = emptyFlow()
        override fun getFavorites(): Flow<List<Recommendation>> = emptyFlow()
        override suspend fun toggleFavorite(redditId: String) { toggleFavoriteCalls.add(redditId) }
        override suspend fun toggleDone(redditId: String) {}
        override suspend fun clearAllFavorites() {}
        override suspend fun syncFromReddit(): DataResult<Int> = DataResult.Success(0)
        override fun getLastSyncTimestamp(): Flow<Long?> = emptyFlow()
    }
}
