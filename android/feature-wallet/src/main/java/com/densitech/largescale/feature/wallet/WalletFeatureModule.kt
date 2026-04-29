package com.densitech.largescale.feature.wallet

import com.densitech.largescale.contracts.AppModule
import com.densitech.largescale.contracts.ModuleContext
import com.densitech.largescale.contracts.ModuleMetadata
import com.densitech.largescale.contracts.ModuleRoute
import com.densitech.largescale.contracts.Role
import com.densitech.largescale.contracts.Routes
import com.densitech.largescale.contracts.UISlot

class WalletFeatureModule : AppModule {

    override val metadata = ModuleMetadata(
        id = "wallet",
        name = "Wallet",
        version = "1.0.0",
        requiredRoles = setOf(Role.ADMIN, Role.STAFF, Role.CUSTOMER),
        priority = 700
    )

    override fun initialize(context: ModuleContext) {
        // Phase 7+: register wallet balance widget
    }

    override fun provideRoutes() = listOf(
        ModuleRoute(route = Routes.WALLET, requiredRole = Role.CUSTOMER)
    )

    override fun provideWidgets(): List<UISlot> = emptyList()
}
