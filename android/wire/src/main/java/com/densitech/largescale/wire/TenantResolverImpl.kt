package com.densitech.largescale.wire

import com.densitech.largescale.contracts.ApiConfig
import com.densitech.largescale.contracts.TenantConfig
import com.densitech.largescale.contracts.TenantSwitchedEvent
import com.densitech.largescale.contracts.TenantTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages the active [TenantConfig] for the application.
 *
 * Phase 3: Ships with two hardcoded tenants (brand-a, brand-b) and a default fallback.
 * Phase 4+: Will load tenant configs from assets/JSON or remote config.
 *
 * Tenant selection is persisted via [StorageManager] (available in Phase 4).
 * Until then, the default tenant is used on every launch.
 */
@Singleton
class TenantResolverImpl @Inject constructor(
    private val eventBus: FlowEventBus
) {
    private val availableTenants: Map<String, TenantConfig> = buildDefaultTenants()

    private val _currentTenant = MutableStateFlow<TenantConfig?>(availableTenants[DEFAULT_TENANT_ID])
    val currentTenant: StateFlow<TenantConfig?> = _currentTenant.asStateFlow()

    // ── Public API ────────────────────────────────────────────────────────────

    fun getAvailableTenants(): List<TenantConfig> = availableTenants.values.toList()

    fun resolveTenantById(tenantId: String): TenantConfig? = availableTenants[tenantId]

    /**
     * Switch the active tenant.
     * Publishes a [TenantSwitchedEvent] so modules can react (e.g., refresh data).
     *
     * @return true if the switch succeeded, false if [tenantId] is unknown.
     */
    fun switchTenant(tenantId: String): Boolean {
        val newTenant = availableTenants[tenantId] ?: return false
        val oldTenant = _currentTenant.value
        _currentTenant.value = newTenant
        eventBus.publish(
            TenantSwitchedEvent(
                oldTenantId = oldTenant?.tenantId ?: "",
                newTenantId = tenantId
            )
        )
        return true
    }

    fun isModuleEnabledForTenant(moduleId: String): Boolean {
        val tenant = _currentTenant.value ?: return true
        return moduleId in tenant.enabledModules
    }

    // ── Defaults ──────────────────────────────────────────────────────────────

    private fun buildDefaultTenants(): Map<String, TenantConfig> = mapOf(
        DEFAULT_TENANT_ID to TenantConfig(
            tenantId = DEFAULT_TENANT_ID,
            displayName = "Default",
            enabledModules = listOf("core", "dashboard", "orders", "inventory", "wallet"),
            theme = TenantTheme(),
            apiConfig = ApiConfig(baseUrl = "https://api.example.com")
        ),
        "brand-a" to TenantConfig(
            tenantId = "brand-a",
            displayName = "Brand A",
            enabledModules = listOf("core", "dashboard", "orders", "inventory", "wallet"),
            theme = TenantTheme(
                primaryColor = "#1976D2",
                secondaryColor = "#42A5F5",
                backgroundColor = "#FFFFFF"
            ),
            apiConfig = ApiConfig(baseUrl = "https://api.brand-a.example.com")
        ),
        "brand-b" to TenantConfig(
            tenantId = "brand-b",
            displayName = "Brand B",
            enabledModules = listOf("core", "dashboard", "wallet"),
            theme = TenantTheme(
                primaryColor = "#388E3C",
                secondaryColor = "#66BB6A",
                backgroundColor = "#F1F8E9"
            ),
            apiConfig = ApiConfig(baseUrl = "https://api.brand-b.example.com")
        )
    )

    companion object {
        private const val DEFAULT_TENANT_ID = "default"
    }
}
