package com.londongems.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface RedditService {

    @GET("r/london/search.json")
    suspend fun getRecommendations(
        @Query("q") query: String = "hidden gems OR best restaurants OR things to do",
        @Query("restrict_sr") restrictSubreddit: Boolean = true,
        @Query("sort") sort: String = "relevance",
        @Query("limit") limit: Int = 50,
    ): RedditListingResponse
}
