package com.densitech.largescale.contracts

import kotlinx.coroutines.flow.StateFlow

/**
 * Navigation route provided by a module.
 *
 * @property route Navigation route string (e.g., "/orders", "/orders/{id}")
 * @property requiredRole Minimum role required to access this route
 */
data class ModuleRoute(
    val route: String,
    val requiredRole: Role = Role.GUEST
)

/**
 * UI slot/widget provided by a module for dynamic composition.
 *
 * @property slotId     Identifier for the slot host (e.g., [SlotIds.HOME_WIDGETS])
 * @property widgetId   Unique identifier for this specific widget
 * @property moduleId   ID of the module that contributes this widget (used by [SlotRegistry.clearModule])
 * @property priority   Display priority — higher value = rendered first (default 100)
 * @property requiredRole Minimum role required to see this widget
 * @property content    Composable content rendered inside the slot
 */
data class UISlot(
    val slotId: String,
    val widgetId: String,
    val moduleId: String,
    val priority: Int = 100,
    val requiredRole: Role = Role.GUEST,
    val content: @Composable () -> Unit
)

/**
 * Placeholder for Composable annotation (actual annotation comes from Compose runtime).
 * This is a marker annotation for the contracts module.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.SOURCE)
annotation class Composable
