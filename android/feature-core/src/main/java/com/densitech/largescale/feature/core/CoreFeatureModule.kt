package com.densitech.largescale.feature.core

import com.densitech.largescale.contracts.AppModule
import com.densitech.largescale.contracts.ModuleContext
import com.densitech.largescale.contracts.ModuleMetadata
import com.densitech.largescale.contracts.ModuleRoute
import com.densitech.largescale.contracts.Role
import com.densitech.largescale.contracts.Routes
import com.densitech.largescale.contracts.UISlot
import com.densitech.largescale.contracts.UserLoggedOutEvent
import com.densitech.largescale.contracts.on

/**
 * Core infrastructure module — handles authentication UI and app entry points.
 *
 * Provides: SPLASH and LOGIN routes (always available, no role restriction).
 * Listens for [UserLoggedOutEvent] to redirect back to login.
 *
 * Priority 1000 — initialized first so navigation entry point is always ready.
 */
class CoreFeatureModule : AppModule {

    override val metadata = ModuleMetadata(
        id = "core",
        name = "Core",
        version = "1.0.0",
        requiredRoles = setOf(Role.ADMIN, Role.STAFF, Role.CUSTOMER, Role.GUEST),
        priority = 1000
    )

    private var moduleContext: ModuleContext? = null

    override fun initialize(context: ModuleContext) {
        moduleContext = context

        // On logout, navigate back to login and clear backstack
        context.eventBus.on<UserLoggedOutEvent> {
            context.navigate(Routes.LOGIN)
        }
    }

    override fun provideRoutes() = listOf(
        ModuleRoute(route = Routes.SPLASH, requiredRole = Role.GUEST),
        ModuleRoute(route = Routes.LOGIN,  requiredRole = Role.GUEST)
    )

    override fun provideWidgets(): List<UISlot> = emptyList()

    override fun onDestroy() {
        moduleContext = null
    }
}
