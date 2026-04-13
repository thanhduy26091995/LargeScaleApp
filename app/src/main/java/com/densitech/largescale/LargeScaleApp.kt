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
import javax.inject.Inject

private const val TAG = "LargeScaleApp"

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

    private fun registerModules() {
        moduleRegistry.register(CoreFeatureModule())
        moduleRegistry.register(DashboardFeatureModule())
        moduleRegistry.register(OrdersFeatureModule())
        moduleRegistry.register(InventoryFeatureModule())
        moduleRegistry.register(WalletFeatureModule())
    }
}
