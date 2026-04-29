package com.densitech.largescale.core.network.interceptor

import com.densitech.largescale.core.storage.StorageManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * OkHttp interceptor that attaches the Bearer token to every outgoing request.
 *
 * Reads the token synchronously from [StorageManager] using [runBlocking].
 * This is acceptable here because OkHttp interceptors run on an I/O thread —
 * blocking it briefly does not affect the main thread.
 *
 * Phase 5+: Replace with a token retrieved from a real auth backend and implement
 * token refresh logic (401 → refresh → retry).
 */
class AuthInterceptor @Inject constructor(
    private val storageManager: StorageManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { storageManager.getString(KEY_AUTH_TOKEN) }

        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }

        return chain.proceed(request)
    }

    companion object {
        const val KEY_AUTH_TOKEN = "auth_token"
    }
}
