package com.densitech.largescale.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.densitech.largescale.contracts.AuthService
import com.densitech.largescale.core.auth.AuthServiceImpl
import com.densitech.largescale.core.network.ApiClient
import com.densitech.largescale.core.network.interceptor.AuthInterceptor
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "largescale_prefs")

/**
 * Hilt module for `:core` — provides authentication, storage, and networking services.
 *
 * Dependency rule: `:core` only depends on `:contracts`, never on `:wire`.
 * The `ApiClient` base URL is fixed to the default here; at runtime, feature modules
 * that need tenant-aware URLs read [ModuleContext.tenantConfig] and call
 * [ApiClient.create] directly, or the `:app` layer re-configures via a qualifier.
 *
 * Services provided:
 * - [DataStore<Preferences>]  — single DataStore file for the app
 * - [StorageManager]          — typed wrapper around DataStore (self-bound via @Inject)
 * - [AuthService]             → [AuthServiceImpl] (mock for Phase 4; swap in Phase 5)
 * - [ApiClient]               — Retrofit client with auth interceptor
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class CoreModule {

    // ── Interface bindings ────────────────────────────────────────────────────

    @Binds
    @Singleton
    abstract fun bindAuthService(impl: AuthServiceImpl): AuthService

    // ── Object provisions ─────────────────────────────────────────────────────

    companion object {

        @Provides
        @Singleton
        fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
            context.dataStore

        /**
         * Provide a default [ApiClient].
         *
         * Base URL defaults to "https://api.example.com".
         * Phase 5+: The `:app` layer can provide a tenant-aware [ApiClient] by reading
         * [ModuleContext.tenantConfig] and overriding this binding with a qualified provider.
         */
        @Provides
        @Singleton
        fun provideApiClient(authInterceptor: AuthInterceptor): ApiClient =
            ApiClient.create(
                baseUrl = "https://api.example.com",
                authInterceptor = authInterceptor,
                isDebug = true // Phase 5+: tie to BuildConfig.DEBUG
            )
    }
}
