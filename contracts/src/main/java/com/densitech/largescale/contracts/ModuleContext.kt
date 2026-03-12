package com.densitech.largescale.contracts

import kotlinx.coroutines.flow.StateFlow

/**
 * Tenant configuration for multi-tenant support.
 * Each tenant can have unique branding, features, and API endpoints.
 *
 * @property tenantId Unique identifier for the tenant
 * @property displayName Public-facing name of the tenant
 * @property enabledModules List of module IDs enabled for this tenant
 * @property theme Theme configuration (colors, typography, etc.)
 * @property apiConfig API endpoint configuration
 */
data class TenantConfig(
    val tenantId: String,
    val displayName: String,
    val enabledModules: List<String> = emptyList(),
    val theme: TenantTheme = TenantTheme(),
    val apiConfig: ApiConfig = ApiConfig()
)

/**
 * Tenant-specific theme configuration.
 */
data class TenantTheme(
    val primaryColor: String = "#6200EE",
    val secondaryColor: String = "#03DAC6",
    val backgroundColor: String = "#FFFFFF",
    val logoUrl: String? = null
)

/**
 * Tenant-specific API configuration.
 */
data class ApiConfig(
    val baseUrl: String = "https://api.example.com",
    val apiKey: String? = null,
    val timeout: Long = 30000
)

/**
 * Context provided to modules during initialization.
 * Provides access to Wire Core services and shared resources.
 */
interface ModuleContext {
    /**
     * Current tenant configuration as a reactive state.
     * Modules can observe this to react to tenant switches.
     */
    val tenantConfig: StateFlow<TenantConfig?>

    /**
     * Current user role.
     */
    val currentRole: StateFlow<Role>

    /**
     * Publish an event to the EventBus.
     */
    suspend fun publishEvent(event: ModuleEvent)

    /**
     * Navigate to a route.
     */
    fun navigate(route: String)

    /**
     * Get application context (for resources, etc.).
     */
    fun getApplicationContext(): Any // Platform-agnostic (would be Context on Android)
}
