package com.londongemsapp.data.repository

import com.londongemsapp.data.local.RecommendationDao
import com.londongemsapp.data.local.entity.RecommendationEntity
import com.londongemsapp.data.remote.RedditApi
import com.londongemsapp.data.remote.RedditDtoMapper
import com.londongemsapp.data.remote.dto.RedditChild
import com.londongemsapp.data.remote.dto.RedditListingData
import com.londongemsapp.data.remote.dto.RedditListingResponse
import com.londongemsapp.data.remote.dto.RedditPostDto
import com.londongemsapp.domain.classifier.CategoryClassifier
import com.londongemsapp.domain.model.Category
import com.londongemsapp.domain.model.ClassificationResult
import com.londongemsapp.domain.model.DataResult
import com.londongemsapp.domain.model.Recommendation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RecommendationRepositoryImplTest {

    private lateinit var fakeDao: FakeRecommendationDao
    private lateinit var fakeApi: FakeRedditApi
    private lateinit var fakePrefs: FakeSharedPreferences
    private lateinit var mapper: RedditDtoMapper
    private lateinit var repository: RecommendationRepositoryImpl

    @Before
    fun setUp() {
        fakeDao = FakeRecommendationDao()
        fakeApi = FakeRedditApi()
        fakePrefs = FakeSharedPreferences()
        mapper = RedditDtoMapper(StubClassifier())
        repository = RecommendationRepositoryImpl(
            redditApi = fakeApi,
            dao = fakeDao,
            mapper = mapper,
            prefs = fakePrefs,
        )
    }

    // -- getRecommendations delegates to correct DAO method --

    @Test
    fun getRecommendations_nullCategory_delegatesToGetAll() = runTest {
        fakeDao.allEntities.value = listOf(makeEntity("1"), makeEntity("2"))

        val result = repository.getRecommendations(null).first()

        assertEquals(2, result.size)
        assertTrue(fakeDao.getAllCalled)
    }

    @Test
    fun getRecommendations_withCategory_delegatesToGetByCategory() = runTest {
        fakeDao.categoryEntities.value = listOf(makeEntity("1", category = Category.FOOD_AND_DRINKS))

        val result = repository.getRecommendations(Category.FOOD_AND_DRINKS).first()

        assertEquals(1, result.size)
        assertEquals(Category.FOOD_AND_DRINKS, result.first().category)
        assertTrue(fakeDao.getByCategoryCalled)
        assertEquals(Category.FOOD_AND_DRINKS, fakeDao.lastRequestedCategory)
    }

    // -- Entity-to-domain mapping --

    @Test
    fun getRecommendations_mapsEntityFieldsToDomain() = runTest {
        val entity = RecommendationEntity(
            redditId = "abc123",
            title = "Best Pub",
            body = "Great place",
            subreddit = "london",
            category = Category.FOOD_AND_DRINKS,
            score = 99,
            url = "https://www.reddit.com/r/london/comments/abc123/",
            thumbnailUrl = "https://i.redd.it/thumb.jpg",
            isFavorite = true,
            isDone = false,
            createdAt = 1700000000000L,
            fetchedAt = 1700001000000L,
        )
        fakeDao.allEntities.value = listOf(entity)

        val result = repository.getRecommendations(null).first().first()

        assertEquals("abc123", result.redditId)
        assertEquals("Best Pub", result.title)
        assertEquals("Great place", result.body)
        assertEquals("london", result.subreddit)
        assertEquals(Category.FOOD_AND_DRINKS, result.category)
        assertEquals(99, result.score)
        assertEquals("https://www.reddit.com/r/london/comments/abc123/", result.url)
        assertEquals("https://i.redd.it/thumb.jpg", result.thumbnailUrl)
        assertEquals(true, result.isFavorite)
        assertEquals(false, result.isDone)
        assertEquals(1700000000000L, result.createdAt)
    }

    @Test
    fun getRecommendation_mapsEntityToDomain() = runTest {
        fakeDao.singleEntity.value = makeEntity("single_1")

        val result = repository.getRecommendation("single_1").first()

        assertEquals("single_1", result?.redditId)
    }

    @Test
    fun getRecommendation_notFound_returnsNull() = runTest {
        fakeDao.singleEntity.value = null

        val result = repository.getRecommendation("nonexistent").first()

        assertEquals(null, result)
    }

    @Test
    fun getFavorites_returnsOnlyFavorites() = runTest {
        fakeDao.favoriteEntities.value = listOf(
            makeEntity("fav1", isFavorite = true),
            makeEntity("fav2", isFavorite = true),
        )

        val result = repository.getFavorites().first()

        assertEquals(2, result.size)
        assertTrue(result.all { it.isFavorite })
    }

    // -- syncFromReddit error handling --

    @Test
    fun syncFromReddit_apiThrows_returnsDataResultError() = runTest {
        fakeApi.shouldThrow = true

        val result = repository.syncFromReddit()

        assertTrue(result is DataResult.Error)
        val error = result as DataResult.Error
        assertEquals("API failure", error.exception.message)
    }

    @Test
    fun syncFromReddit_success_returnsEntityCount() = runTest {
        fakeApi.hotResponse = makeListingResponse(
            makePostDto("p1", "Post One", "Body of post one"),
            makePostDto("p2", "Post Two", "Body of post two"),
        )
        fakeApi.topResponse = makeListingResponse()

        val result = repository.syncFromReddit()

        assertTrue(result is DataResult.Success)
        // 2 posts from "london" subreddit + 2 from "LondonSocialClub" (same fakeApi)
        val count = (result as DataResult.Success).data
        assertEquals(4, count)
    }

    @Test
    fun syncFromReddit_success_savesLastSyncTimestamp() = runTest {
        fakeApi.hotResponse = makeListingResponse()
        fakeApi.topResponse = makeListingResponse()

        repository.syncFromReddit()

        assertTrue(fakePrefs.savedLong > 0)
    }

    @Test
    fun syncFromReddit_success_upsertsCalled() = runTest {
        fakeApi.hotResponse = makeListingResponse(
            makePostDto("p1", "Post", "Body content here"),
        )
        fakeApi.topResponse = makeListingResponse()

        repository.syncFromReddit()

        assertTrue(fakeDao.upsertCalled)
    }

    @Test
    fun toggleFavorite_delegatesToDao() = runTest {
        repository.toggleFavorite("some_id")

        assertEquals("some_id", fakeDao.lastToggledFavoriteId)
    }

    @Test
    fun toggleDone_delegatesToDao() = runTest {
        repository.toggleDone("some_id")

        assertEquals("some_id", fakeDao.lastToggledDoneId)
    }

    // -- Helpers and Fakes --

    private fun makeEntity(
        id: String,
        category: Category = Category.UNCATEGORIZED,
        isFavorite: Boolean = false,
    ) = RecommendationEntity(
        redditId = id,
        title = "Title $id",
        body = "Body $id",
        subreddit = "london",
        category = category,
        score = 10,
        url = "https://www.reddit.com/r/london/comments/$id/",
        thumbnailUrl = null,
        isFavorite = isFavorite,
        isDone = false,
        createdAt = 1700000000000L,
        fetchedAt = 1700001000000L,
    )

    private fun makePostDto(
        id: String,
        title: String,
        body: String,
    ) = RedditPostDto(
        id = id,
        title = title,
        selftext = body,
        subreddit = "london",
        score = 10,
        permalink = "/r/london/comments/$id/post/",
        createdUtc = 1700000000.0,
    )

    private fun makeListingResponse(
        vararg posts: RedditPostDto,
    ) = RedditListingResponse(
        kind = "Listing",
        data = RedditListingData(
            children = posts.map { RedditChild(kind = "t3", data = it) },
        ),
    )

    private class StubClassifier : CategoryClassifier {
        override fun classify(
            subreddit: String,
            title: String,
            body: String,
            flair: String?,
        ) = ClassificationResult(Category.UNCATEGORIZED, 0.5f)
    }

    private class FakeRecommendationDao : RecommendationDao {
        val allEntities = MutableStateFlow<List<RecommendationEntity>>(emptyList())
        val categoryEntities = MutableStateFlow<List<RecommendationEntity>>(emptyList())
        val singleEntity = MutableStateFlow<RecommendationEntity?>(null)
        val favoriteEntities = MutableStateFlow<List<RecommendationEntity>>(emptyList())

        var getAllCalled = false
        var getByCategoryCalled = false
        var lastRequestedCategory: Category? = null
        var upsertCalled = false
        var lastToggledFavoriteId: String? = null
        var lastToggledDoneId: String? = null

        override fun getAll(): Flow<List<RecommendationEntity>> {
            getAllCalled = true
            return allEntities
        }

        override fun getByCategory(category: Category): Flow<List<RecommendationEntity>> {
            getByCategoryCalled = true
            lastRequestedCategory = category
            return categoryEntities
        }

        override fun getById(redditId: String): Flow<RecommendationEntity?> = singleEntity

        override fun getFavorites(): Flow<List<RecommendationEntity>> = favoriteEntities

        override suspend fun insertOrIgnore(
            redditId: String,
            title: String,
            body: String,
            subreddit: String,
            category: String,
            score: Int,
            url: String,
            thumbnailUrl: String?,
            createdAt: Long,
            fetchedAt: Long,
        ) {}

        override suspend fun updateNetworkFields(
            redditId: String,
            title: String,
            body: String,
            score: Int,
            category: String,
            thumbnailUrl: String?,
            fetchedAt: Long,
        ) {}

        override suspend fun upsertFromNetwork(entities: List<RecommendationEntity>) {
            upsertCalled = true
        }

        override suspend fun toggleFavorite(redditId: String) {
            lastToggledFavoriteId = redditId
        }

        override suspend fun toggleDone(redditId: String) {
            lastToggledDoneId = redditId
        }

        override suspend fun deleteAll() {}
    }

    private class FakeRedditApi : RedditApi {
        var shouldThrow = false
        var hotResponse = RedditListingResponse(
            kind = "Listing",
            data = RedditListingData(children = emptyList()),
        )
        var topResponse = RedditListingResponse(
            kind = "Listing",
            data = RedditListingData(children = emptyList()),
        )

        override suspend fun getHotPosts(
            subreddit: String,
            limit: Int,
            after: String?,
            rawJson: Int,
        ): RedditListingResponse {
            if (shouldThrow) throw RuntimeException("API failure")
            return hotResponse
        }

        override suspend fun getTopPosts(
            subreddit: String,
            timeframe: String,
            limit: Int,
            rawJson: Int,
        ): RedditListingResponse {
            if (shouldThrow) throw RuntimeException("API failure")
            return topResponse
        }
    }

    /**
     * Minimal SharedPreferences fake that only tracks the last putLong call.
     * The repository only uses getLong and edit().putLong().apply().
     */
    private class FakeSharedPreferences : android.content.SharedPreferences {
        var savedLong: Long = 0L
        private var storedLong: Long = 0L

        override fun getLong(key: String?, defValue: Long): Long = storedLong

        override fun edit(): android.content.SharedPreferences.Editor = FakeEditor()

        private inner class FakeEditor : android.content.SharedPreferences.Editor {
            private var pendingLong: Long = 0L

            override fun putLong(key: String?, value: Long): android.content.SharedPreferences.Editor {
                pendingLong = value
                return this
            }

            override fun apply() {
                savedLong = pendingLong
                storedLong = pendingLong
            }

            override fun commit(): Boolean {
                apply()
                return true
            }

            // Unused stubs required by the interface
            override fun putString(key: String?, value: String?) = this
            override fun putStringSet(key: String?, values: MutableSet<String>?) = this
            override fun putInt(key: String?, value: Int) = this
            override fun putFloat(key: String?, value: Float) = this
            override fun putBoolean(key: String?, value: Boolean) = this
            override fun remove(key: String?) = this
            override fun clear() = this
        }

        // Unused stubs required by the interface
        override fun getAll(): MutableMap<String, *> = mutableMapOf<String, Any>()
        override fun getString(key: String?, defValue: String?): String? = defValue
        override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? = defValues
        override fun getInt(key: String?, defValue: Int): Int = defValue
        override fun getFloat(key: String?, defValue: Float): Float = defValue
        override fun getBoolean(key: String?, defValue: Boolean): Boolean = defValue
        override fun contains(key: String?): Boolean = false
        override fun registerOnSharedPreferenceChangeListener(listener: android.content.SharedPreferences.OnSharedPreferenceChangeListener?) {}
        override fun unregisterOnSharedPreferenceChangeListener(listener: android.content.SharedPreferences.OnSharedPreferenceChangeListener?) {}
    }
}
