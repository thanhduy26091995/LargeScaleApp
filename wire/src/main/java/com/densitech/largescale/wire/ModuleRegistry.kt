package com.densitech.largescale.wire

import android.util.Log
import com.densitech.largescale.contracts.AppModule
import com.densitech.largescale.contracts.ModuleContext
import com.densitech.largescale.contracts.ModuleInitializedEvent
import com.densitech.largescale.contracts.Role
import com.densitech.largescale.contracts.TenantConfig
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "ModuleRegistry"

/**
 * Central registry that manages the lifecycle of all [AppModule] instances.
 *
 * Responsibilities:
 * - Accept module registrations from [LargeScaleApp]
 * - Filter modules by active [Role] and [TenantConfig]
 * - Drive module initialization and teardown
 *
 * Module registration happens in [LargeScaleApp.onCreate] before Wire Core
 * calls [initializeAll]. Modules are processed in descending [AppModule.metadata.priority] order.
 */
@Singleton
class ModuleRegistry @Inject constructor() {

    private val modules = mutableListOf<AppModule>()
    private val initializedIds = mutableSetOf<String>()

    // ── Registration ──────────────────────────────────────────────────────────

    /**
     * Register a feature module. Must be called before [initializeAll].
     * Duplicate registrations (same [AppModule.metadata.id]) are ignored.
     */
    fun register(module: AppModule) {
        if (modules.any { it.metadata.id == module.metadata.id }) {
            Log.w(TAG, "Module '${module.metadata.id}' already registered — skipping duplicate")
            return
        }
        modules.add(module)
        Log.d(TAG, "Registered module '${module.metadata.id}' (v${module.metadata.version})")
    }

    // ── Resolution ────────────────────────────────────────────────────────────

    /**
     * Return modules enabled for the given [role] and [tenantConfig],
     * sorted by descending [AppModule.metadata.priority].
     *
     * A module passes the filter when:
     * 1. The user's role is in [AppModule.metadata.requiredRoles]
     * 2. `supportedTenants` is empty (= all tenants) OR tenant ID is listed
     * 3. The module ID is in [TenantConfig.enabledModules] (when tenant config is present)
     */
    fun resolve(role: Role, tenantConfig: TenantConfig?): List<AppModule> {
        return modules
            .filter { module ->
                val roleAllowed = role in module.metadata.requiredRoles
                val tenantAllowed = tenantConfig == null ||
                        module.metadata.supportedTenants.isEmpty() ||
                        tenantConfig.tenantId in module.metadata.supportedTenants
                val enabledForTenant = tenantConfig == null ||
                        module.metadata.id in tenantConfig.enabledModules
                roleAllowed && tenantAllowed && enabledForTenant
            }
            .sortedByDescending { it.metadata.priority }
    }

    /**
     * Look up a registered module by its ID. Returns null if not found.
     */
    fun getModuleById(id: String): AppModule? = modules.find { it.metadata.id == id }

    /** All registered modules regardless of role / tenant. */
    fun getAllModules(): List<AppModule> = modules.toList()

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    /**
     * Initialize all modules that pass the role + tenant filter.
     *
     * Each module's [AppModule.initialize] is wrapped in a try-catch so a single
     * failing module cannot prevent the rest of the app from starting.
     *
     * After successful initialization, a [ModuleInitializedEvent] is published
     * so other modules can react (e.g., cross-module dependency chains).
     */
    fun initializeAll(context: ModuleContext, role: Role, tenantConfig: TenantConfig?) {
        val eligible = resolve(role, tenantConfig)
        val pending = eligible.filter { it.metadata.id !in initializedIds }
        Log.i(TAG, "Initializing ${pending.size} new / ${eligible.size} eligible / ${modules.size} total modules for role=$role")

        pending.forEach { module ->
            try {
                module.initialize(context)
                initializedIds.add(module.metadata.id)
                context.eventBus.publish(ModuleInitializedEvent(moduleId = module.metadata.id))
                Log.d(TAG, "Initialized '${module.metadata.id}'")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize '${module.metadata.id}'", e)
                // Continue — one bad module should not block the app
            }
        }
    }

    /**
     * Destroy all registered modules. Called on app shutdown or before re-initialization
     * (e.g., tenant switch that requires a full restart).
     */
    fun destroyAll() {
        modules.forEach { module ->
            try {
                module.onDestroy()
            } catch (e: Exception) {
                Log.e(TAG, "Error destroying '${module.metadata.id}'", e)
            }
        }
        initializedIds.clear()
        Log.i(TAG, "All modules destroyed")
    }
}
