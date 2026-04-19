package com.londongemsapp.data.remote

import com.londongemsapp.BuildConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Credentials
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
private data class TokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Long,
)

@Singleton
class RedditAuthInterceptor @Inject constructor() : Interceptor {

    private val tokenClient = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    @Volatile private var accessToken: String? = null
    @Volatile private var tokenExpiresAt: Long = 0L

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = getTokenOrNull()

        val requestBuilder = chain.request().newBuilder()
            .header("User-Agent", USER_AGENT)

        if (token != null) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }

    @Synchronized
    private fun getTokenOrNull(): String? {
        val now = System.currentTimeMillis()
        if (accessToken != null && now < tokenExpiresAt) return accessToken

        if (BuildConfig.REDDIT_CLIENT_ID == "YOUR_REDDIT_CLIENT_ID") return null

        return try {
            val body = FormBody.Builder()
                .add("grant_type", GRANT_TYPE)
                .add("device_id", DEVICE_ID)
                .build()

            val credential = Credentials.basic(BuildConfig.REDDIT_CLIENT_ID, "")

            val request = Request.Builder()
                .url(TOKEN_URL)
                .post(body)
                .header("Authorization", credential)
                .header("User-Agent", USER_AGENT)
                .build()

            val response = tokenClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: return null

            if (!response.isSuccessful) return null

            val tokenResponse = json.decodeFromString<TokenResponse>(responseBody)
            accessToken = tokenResponse.accessToken
            tokenExpiresAt = now + (tokenResponse.expiresIn - 60) * 1000L
            accessToken
        } catch (_: Exception) {
            null
        }
    }

    companion object {
        private const val TOKEN_URL = "https://www.reddit.com/api/v1/access_token"
        private const val GRANT_TYPE = "https://oauth.reddit.com/grants/installed_client"
        private const val DEVICE_ID = "DO_NOT_TRACK_THIS_DEVICE"
        private const val USER_AGENT = "android:com.londongemsapp:v1.0"
    }
}
