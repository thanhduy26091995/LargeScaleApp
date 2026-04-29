package com.densitech.largescale.core.network

import com.densitech.largescale.core.network.interceptor.AuthInterceptor
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Factory for creating [Retrofit] instances configured for a specific base URL.
 *
 * Created and provided as a singleton by [CoreModule].
 * Feature modules that need an API service call [createService] with their interface type.
 *
 * Usage:
 * ```
 * val orderApi = apiClient.createService<OrderApi>()
 * ```
 */
class ApiClient(
    val retrofit: Retrofit
) {
    /** Create a Retrofit service implementation for the given API interface [T]. */
    inline fun <reified T> createService(): T = retrofit.create(T::class.java)

    companion object {
        /**
         * Build an [ApiClient] with the given [baseUrl] and interceptors.
         *
         * @param baseUrl       Base URL (must end with "/")
         * @param authInterceptor Attaches Bearer token to requests
         * @param isDebug       When true, full request/response bodies are logged
         */
        fun create(
            baseUrl: String,
            authInterceptor: AuthInterceptor,
            isDebug: Boolean = false
        ): ApiClient {
            val json = Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            }

            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = if (isDebug) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.BASIC
                }
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(normalizeBaseUrl(baseUrl))
                .client(okHttpClient)
                .addConverterFactory(json.asConverterFactory("application/json; charset=UTF8".toMediaType()))
                .build()

            return ApiClient(retrofit)
        }

        /** Ensure the base URL always ends with "/" as required by Retrofit. */
        private fun normalizeBaseUrl(url: String): String =
            if (url.endsWith("/")) url else "$url/"
    }
}
