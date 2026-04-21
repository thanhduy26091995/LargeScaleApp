package com.densitech.largescale.feature.inventory

import com.densitech.largescale.contracts.AppModule
import com.densitech.largescale.contracts.ModuleContext
import com.densitech.largescale.contracts.ModuleMetadata
import com.densitech.largescale.contracts.ModuleRoute
import com.densitech.largescale.contracts.Role
import com.densitech.largescale.contracts.Routes
import com.densitech.largescale.contracts.SlotIds
import com.densitech.largescale.contracts.UISlot
import com.densitech.largescale.feature.inventory.ui.InventoryScreen

class InventoryFeatureModule : AppModule {

    override val metadata = ModuleMetadata(
        id = "inventory",
        name = "Inventory",
        version = "1.0.0",
        requiredRoles = setOf(Role.ADMIN, Role.STAFF),
        priority = 800
    )

    override fun initialize(context: ModuleContext) {
        // Widgets are registered via provideWidgets() — no additional setup needed here
    }

    override fun provideRoutes() = listOf(
        ModuleRoute(route = Routes.INVENTORY, requiredRole = Role.STAFF)
    )

    override fun provideWidgets(): List<UISlot> = listOf(
        UISlot(
            slotId = SlotIds.HOME_WIDGETS,
            widgetId = "inventory_summary",
            moduleId = "inventory",
            priority = 79,
            requiredRole = Role.CUSTOMER
        ) { InventoryScreen() })
}
