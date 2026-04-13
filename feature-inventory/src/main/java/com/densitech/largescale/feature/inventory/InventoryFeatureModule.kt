package com.densitech.largescale.feature.inventory

import com.densitech.largescale.contracts.AppModule
import com.densitech.largescale.contracts.ModuleContext
import com.densitech.largescale.contracts.ModuleMetadata
import com.densitech.largescale.contracts.ModuleRoute
import com.densitech.largescale.contracts.Role
import com.densitech.largescale.contracts.Routes
import com.densitech.largescale.contracts.UISlot

class InventoryFeatureModule : AppModule {

    override val metadata = ModuleMetadata(
        id = "inventory",
        name = "Inventory",
        version = "1.0.0",
        requiredRoles = setOf(Role.ADMIN, Role.STAFF),
        priority = 800
    )

    override fun initialize(context: ModuleContext) {
        // Phase 7+: register inventory widget to home screen
    }

    override fun provideRoutes() = listOf(
        ModuleRoute(route = Routes.INVENTORY, requiredRole = Role.STAFF)
    )

    override fun provideWidgets(): List<UISlot> = emptyList()
}
