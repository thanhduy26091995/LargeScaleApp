package com.densitech.largescale

import android.app.Application
import android.util.Log
import com.densitech.largescale.contracts.ModuleContext
import com.densitech.largescale.feature.core.CoreFeatureModule
import com.densitech.largescale.feature.dashboard.DashboardFeatureModule
import com.densitech.largescale.feature.inventory.InventoryFeatureModule
import com.densitech.largescale.feature.orders.OrdersFeatureModule
import com.densitech.largescale.feature.wallet.WalletFeatureModule
import com.densitech.largescale.wire.ModuleRegistry
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "LargeScaleApp"

@HiltAndroidApp
class LargeScaleApp : Application() {

    @Inject lateinit var moduleRegistry: ModuleRegistry
    @Inject lateinit var moduleContext: ModuleContext

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Initializing Wire Core")
        registerModules()

        // First pass — Role.GUEST: initializes only modules that allow guests (e.g. CoreFeatureModule)
        moduleRegistry.initializeAll(
            context = moduleContext,
            role = moduleContext.currentRole.value,
            tenantConfig = moduleContext.tenantConfig.value
        )
        Log.i(TAG, "Wire Core ready — ${moduleRegistry.getAllModules().size} modules registered")

        // Re-initialize when role changes after login; already-initialized modules are skipped
        appScope.launch {
            moduleContext.currentRole
                .drop(1) // skip the initial GUEST emission already handled above
                .collect { role ->
                    Log.i(TAG, "Role changed to $role — running deferred module initialization")
                    moduleRegistry.initializeAll(
                        context = moduleContext,
                        role = role,
                        tenantConfig = moduleContext.tenantConfig.value
                    )
                }
        }
    }

    private fun registerModules() {
        moduleRegistry.register(CoreFeatureModule())
        moduleRegistry.register(DashboardFeatureModule())
        moduleRegistry.register(OrdersFeatureModule())
        moduleRegistry.register(InventoryFeatureModule())
        moduleRegistry.register(WalletFeatureModule())
    }
}
