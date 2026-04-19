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

class ToggleDoneUseCaseTest {

    private val fakeRepository = FakeRepository()
    private val useCase = ToggleDoneUseCase(fakeRepository)

    @Test
    fun `invoke delegates to repository toggleDone`() = runBlocking {
        useCase("post_456")

        assertTrue(fakeRepository.toggleDoneCalls.isNotEmpty())
        assertEquals("post_456", fakeRepository.toggleDoneCalls.first())
    }

    @Test
    fun `invoke passes correct redditId`() = runBlocking {
        useCase("first")
        useCase("second")

        assertEquals(listOf("first", "second"), fakeRepository.toggleDoneCalls)
    }

    private class FakeRepository : RecommendationRepository {
        val toggleDoneCalls = mutableListOf<String>()

        override fun getRecommendations(category: Category?): Flow<List<Recommendation>> = emptyFlow()
        override fun getRecommendation(redditId: String): Flow<Recommendation?> = emptyFlow()
        override fun getFavorites(): Flow<List<Recommendation>> = emptyFlow()
        override suspend fun toggleFavorite(redditId: String) {}
        override suspend fun toggleDone(redditId: String) { toggleDoneCalls.add(redditId) }
        override suspend fun clearAllFavorites() {}
        override suspend fun syncFromReddit(): DataResult<Int> = DataResult.Success(0)
        override fun getLastSyncTimestamp(): Flow<Long?> = emptyFlow()
    }
}
