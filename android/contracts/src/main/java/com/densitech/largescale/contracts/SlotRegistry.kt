package com.densitech.largescale.contracts

/**
 * Known slot host IDs used across the app.
 *
 * Modules use these constants when registering widgets via [UISlot.slotId].
 * Host screens use them when querying [SlotRegistry.getSlotsForHost].
 */
object SlotIds {
    /** Dashboard home screen — primary widget area. */
    const val HOME_WIDGETS = "home_widgets"

    /** Dashboard header — branding or summary info. */
    const val DASHBOARD_HEADER = "dashboard_header"

    /** Profile screen — extra actions contributed by modules. */
    const val PROFILE_ACTIONS = "profile_actions"

    /** Bottom navigation bar — extra nav items contributed by modules. */
    const val BOTTOM_BAR_ACTIONS = "bottom_bar_actions"

    /** Quick-action FAB area on the home screen. */
    const val HOME_QUICK_ACTIONS = "home_quick_actions"
}

/**
 * Registry for dynamic UI slot composition.
 *
 * Modules register their widgets during [AppModule.initialize].
 * Host screens (e.g., Dashboard) query this registry to render contributed content.
 *
 * Example — module registering a widget:
 * ```
 * override fun provideWidgets() = listOf(
 *     UISlot(
 *         slotId    = SlotIds.HOME_WIDGETS,
 *         widgetId  = "orders_summary",
 *         moduleId  = "orders",
 *         priority  = 80,
 *         requiredRole = Role.STAFF
 *     ) { OrdersSummaryWidget() }
 * )
 * ```
 *
 * Example — host screen rendering:
 * ```
 * val slots = slotRegistry.getSlotsForHost(SlotIds.HOME_WIDGETS, userRole)
 * slots.forEach { slot -> slot.content() }
 * ```
 */
interface SlotRegistry {
    /**
     * Register a widget slot provided by a module.
     * If a widget with the same [UISlot.widgetId] already exists it is replaced.
     */
    fun register(slot: UISlot)

    /**
     * Retrieve all widgets registered for [slotId] that the given [userRole] is allowed to see,
     * sorted by descending [UISlot.priority].
     */
    fun getSlotsForHost(slotId: String, userRole: Role): List<UISlot>

    /**
     * Unregister a specific widget by its [UISlot.widgetId].
     */
    fun unregister(widgetId: String)

    /**
     * Unregister all widgets contributed by a given module.
     * Called when a module is torn down or disabled at runtime.
     */
    fun clearModule(moduleId: String)
}
