package com.alejojamc


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path

class Client(
    private val apiKey: String,
    private val baseUrl: String = "http://localhost:8001"
) {
    private val retrofit: Retrofit = createRetrofit()
    private val api: ApiService = retrofit.create(ApiService::class.java)

    suspend fun getUser(userId: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = api.getUser(userId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(SDKException.ApiError("Failed to fetch user", e))
        }
    }

    private fun createRetrofit(): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $apiKey")
                    .build()
                chain.proceed(request)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .build()
    }
}
