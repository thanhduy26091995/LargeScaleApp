package com.densitech.largescale.feature.orders

import com.densitech.largescale.contracts.AppModule
import com.densitech.largescale.contracts.ModuleContext
import com.densitech.largescale.contracts.ModuleMetadata
import com.densitech.largescale.contracts.ModuleRoute
import com.densitech.largescale.contracts.Role
import com.densitech.largescale.contracts.Routes
import com.densitech.largescale.contracts.SlotIds
import com.densitech.largescale.contracts.UISlot
import com.densitech.largescale.feature.orders.ui.widget.OrdersSummaryWidget

class OrdersFeatureModule : AppModule {

    override val metadata = ModuleMetadata(
        id = "orders",
        name = "Orders",
        version = "1.0.0",
        requiredRoles = setOf(Role.ADMIN, Role.STAFF),
        priority = 800
    )

    override fun initialize(context: ModuleContext) {
        context.slotRegistry.register(
            UISlot(
                slotId = SlotIds.HOME_WIDGETS,
                widgetId = "orders_summary",
                moduleId = "orders",
                priority = 80,
                requiredRole = Role.CUSTOMER
            ) { OrdersSummaryWidget() }
        )
    }

    override fun provideRoutes() = listOf(
        ModuleRoute(route = Routes.ORDERS, requiredRole = Role.STAFF),
        ModuleRoute(route = Routes.ORDER_DETAIL, requiredRole = Role.STAFF)
    )

    override fun onDestroy() {
        // SlotRegistry.clearModule called by Wire Core on tenant switch
    }
}
