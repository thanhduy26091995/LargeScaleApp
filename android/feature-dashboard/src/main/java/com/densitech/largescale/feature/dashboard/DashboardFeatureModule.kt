package com.densitech.largescale.feature.dashboard

import androidx.compose.material3.Text
import com.densitech.largescale.contracts.AppModule
import com.densitech.largescale.contracts.ModuleContext
import com.densitech.largescale.contracts.ModuleMetadata
import com.densitech.largescale.contracts.ModuleRoute
import com.densitech.largescale.contracts.Role
import com.densitech.largescale.contracts.Routes
import com.densitech.largescale.contracts.UISlot

class DashboardFeatureModule : AppModule {

    override val metadata = ModuleMetadata(
        id = "dashboard",
        name = "Dashboard",
        version = "1.0.0",
        requiredRoles = setOf(Role.ADMIN, Role.STAFF, Role.CUSTOMER, Role.GUEST),
        priority = 900
    )

    override fun initialize(context: ModuleContext) {
        // Dashboard is a slot host — it renders widgets from SlotRegistry.
        // No widgets to register here.
    }

    override fun provideRoutes() = listOf(
        ModuleRoute(route = Routes.HOME, requiredRole = Role.GUEST)
    )

    override fun provideWidgets(): List<UISlot> = emptyList()
}
