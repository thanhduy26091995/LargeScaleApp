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
 * Context provided to each module during [AppModule.initialize].
 *
 * Acts as the module's gateway to all Wire Core services.
 * Modules must not hold a reference to this beyond their own lifecycle.
 *
 * Typical usage inside a module:
 * ```
 * override fun initialize(context: ModuleContext) {
 *     // Subscribe to events
 *     context.eventBus.on<UserAuthenticatedEvent> { loadUserData(it.role) }
 *
 *     // Register widgets
 *     context.slotRegistry.register(myWidget)
 *
 *     // React to tenant changes
 *     context.tenantConfig.onEach { config -> applyTheme(config) }
 * }
 * ```
 */
interface ModuleContext {
    /**
     * Current tenant configuration as a reactive state.
     * Emits a new value whenever the tenant switches or its config is updated.
     */
    val tenantConfig: StateFlow<TenantConfig?>

    /**
     * Current authenticated user role.
     * Emits [Role.GUEST] when no user is authenticated.
     */
    val currentRole: StateFlow<Role>

    /**
     * Cross-module event bus.
     * Use [EventBus.publish] to emit events and [EventBus.on] / [EventBus.subscribe] to listen.
     * Cancel the returned [kotlinx.coroutines.Job] in [AppModule.onDestroy] to avoid leaks.
     */
    val eventBus: EventBus

    /**
     * Dynamic UI slot registry.
     * Modules register their widgets here during [AppModule.initialize].
     * Call [SlotRegistry.clearModule] in [AppModule.onDestroy].
     */
    val slotRegistry: SlotRegistry

    /**
     * Navigate to a route defined by any module.
     * Prefer publishing a [NavigationRequestedEvent] for cross-module navigation.
     */
    fun navigate(route: String)

    /**
     * Returns the Android application [android.content.Context].
     * Typed as [Any] to keep the contracts module platform-agnostic.
     */
    fun getApplicationContext(): Any
}
