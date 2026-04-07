package com.densitech.largescale

import android.app.Application
import android.util.Log
import com.densitech.largescale.contracts.ModuleContext
import com.densitech.largescale.wire.ModuleRegistry
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

private const val TAG = "LargeScaleApp"

/**
 * Application class — entry point for Wire Core initialization.
 *
 * Responsibilities:
 * 1. Register all feature modules (Phase 6: modules added here)
 * 2. Call [ModuleRegistry.initializeAll] to drive module lifecycle
 *
 * Module registration order:
 * - Core infrastructure modules first (higher priority)
 * - Feature modules in any order (priority field controls init order)
 *
 * Example (Phase 6):
 * ```
 * private fun registerModules() {
 *     moduleRegistry.register(CoreFeatureModule())
 *     moduleRegistry.register(DashboardFeatureModule())
 *     moduleRegistry.register(OrdersFeatureModule())
 *     moduleRegistry.register(InventoryFeatureModule())
 *     moduleRegistry.register(WalletFeatureModule())
 * }
 * ```
 */
@HiltAndroidApp
class LargeScaleApp : Application() {

    @Inject lateinit var moduleRegistry: ModuleRegistry
    @Inject lateinit var moduleContext: ModuleContext

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Initializing Wire Core")
        registerModules()
        moduleRegistry.initializeAll(
            context = moduleContext,
            role = moduleContext.currentRole.value,
            tenantConfig = moduleContext.tenantConfig.value
        )
        Log.i(TAG, "Wire Core ready — ${moduleRegistry.getAllModules().size} modules registered")
    }

    /**
     * Register feature modules.
     * Phase 6 will populate this with concrete module instances.
     */
    private fun registerModules() {
        // Feature modules registered here in Phase 6
        // e.g., moduleRegistry.register(CoreFeatureModule())
    }
}
