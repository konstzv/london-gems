package com.londongemsapp.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.londongemsapp.data.local.entity.RecommendationEntity
import com.londongemsapp.domain.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface RecommendationDao {

    @Query("SELECT * FROM recommendations ORDER BY createdAt DESC")
    fun getAll(): Flow<List<RecommendationEntity>>

    @Query("SELECT * FROM recommendations WHERE category = :category ORDER BY createdAt DESC")
    fun getByCategory(category: Category): Flow<List<RecommendationEntity>>

    @Query("SELECT * FROM recommendations WHERE redditId = :redditId")
    fun getById(redditId: String): Flow<RecommendationEntity?>

    @Query("SELECT * FROM recommendations WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavorites(): Flow<List<RecommendationEntity>>

    // INSERT OR IGNORE leaves existing rows untouched (preserving isFavorite and isDone).
    // The follow-up UPDATE overwrites only network-sourced fields on the already-present row.
    @Query(
        """
        INSERT OR IGNORE INTO recommendations
            (redditId, title, body, subreddit, category, score, url, thumbnailUrl, isFavorite, isDone, createdAt, fetchedAt)
        VALUES
            (:redditId, :title, :body, :subreddit, :category, :score, :url, :thumbnailUrl, 0, 0, :createdAt, :fetchedAt)
        """
    )
    suspend fun insertOrIgnore(
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
    )

    @Query(
        """
        UPDATE recommendations SET
            title = :title,
            body = :body,
            score = :score,
            category = :category,
            thumbnailUrl = :thumbnailUrl,
            fetchedAt = :fetchedAt
        WHERE redditId = :redditId
        """
    )
    suspend fun updateNetworkFields(
        redditId: String,
        title: String,
        body: String,
        score: Int,
        category: String,
        thumbnailUrl: String?,
        fetchedAt: Long,
    )

    @Transaction
    suspend fun upsertFromNetwork(entities: List<RecommendationEntity>) {
        for (entity in entities) {
            insertOrIgnore(
                redditId = entity.redditId,
                title = entity.title,
                body = entity.body,
                subreddit = entity.subreddit,
                category = entity.category.name,
                score = entity.score,
                url = entity.url,
                thumbnailUrl = entity.thumbnailUrl,
                createdAt = entity.createdAt,
                fetchedAt = entity.fetchedAt,
            )
            updateNetworkFields(
                redditId = entity.redditId,
                title = entity.title,
                body = entity.body,
                score = entity.score,
                category = entity.category.name,
                thumbnailUrl = entity.thumbnailUrl,
                fetchedAt = entity.fetchedAt,
            )
        }
    }

    @Query("UPDATE recommendations SET isFavorite = NOT isFavorite WHERE redditId = :redditId")
    suspend fun toggleFavorite(redditId: String)

    @Query("UPDATE recommendations SET isDone = NOT isDone WHERE redditId = :redditId")
    suspend fun toggleDone(redditId: String)

    @Query("DELETE FROM recommendations")
    suspend fun deleteAll()
}
