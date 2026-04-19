package com.londongemsapp.data.remote

import com.londongemsapp.data.remote.dto.RedditListingResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RedditApi {

    @GET("r/{subreddit}/hot.json")
    suspend fun getHotPosts(
        @Path("subreddit") subreddit: String,
        @Query("limit") limit: Int = 50,
        @Query("after") after: String? = null,
        @Query("raw_json") rawJson: Int = 1,
    ): RedditListingResponse

    @GET("r/{subreddit}/top.json")
    suspend fun getTopPosts(
        @Path("subreddit") subreddit: String,
        @Query("t") timeframe: String = "month",
        @Query("limit") limit: Int = 50,
        @Query("raw_json") rawJson: Int = 1,
    ): RedditListingResponse
}
