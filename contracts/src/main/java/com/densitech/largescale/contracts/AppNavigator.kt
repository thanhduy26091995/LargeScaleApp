package com.densitech.largescale.contracts

import kotlinx.coroutines.flow.StateFlow

/**
 * Centralized navigation service for the application.
 * Abstracts navigation implementation details from feature modules.
 *
 * Wire Core provides the implementation, modules consume the interface.
 */
interface AppNavigator {
    /**
     * Current navigation route as a reactive state.
     */
    val currentRoute: StateFlow<String?>

    /**
     * Navigate to a specific route.
     *
     * @param route Navigation destination (e.g., "/orders", "/orders/123")
     * @param args Optional navigation arguments
     * @param options Navigation options (e.g., clear backstack, single top)
     */
    fun navigate(
        route: String,
        args: NavigationArgs = NavigationArgs(),
        options: NavigationOptions = NavigationOptions()
    )

    /**
     * Navigate back to the previous screen.
     *
     * @return true if navigation was successful, false if already at root
     */
    fun navigateBack(): Boolean

    /**
     * Navigate up in the navigation hierarchy.
     */
    fun navigateUp()

    /**
     * Navigate to a route and clear the entire backstack.
     * Useful for logout scenarios or switching tenant contexts.
     *
     * @param route Destination route
     */
    fun navigateAndClearBackstack(route: String)

    /**
     * Check if a route exists in the navigation graph.
     *
     * @param route Route to check
     * @return true if the route is registered
     */
    fun isRouteRegistered(route: String): Boolean

    /**
     * Get the navigation backstack depth.
     *
     * @return Number of entries in the backstack
     */
    fun getBackstackDepth(): Int
}

/**
 * Arguments passed during navigation.
 * Supports both string-based and typed arguments.
 */
data class NavigationArgs(
    val stringArgs: Map<String, String> = emptyMap(),
    val intArgs: Map<String, Int> = emptyMap(),
    val boolArgs: Map<String, Boolean> = emptyMap()
) {
    companion object {
        /**
         * Create NavigationArgs with a single string argument.
         */
        fun single(key: String, value: String): NavigationArgs {
            return NavigationArgs(stringArgs = mapOf(key to value))
        }
    }
}

/**
 * Options for navigation behavior.
 */
data class NavigationOptions(
    /**
     * Clear the entire backstack before navigating.
     */
    val clearBackstack: Boolean = false,

    /**
     * Launch as single top (avoid duplicate screens).
     */
    val singleTop: Boolean = false,

    /**
     * Pop up to a specific route before navigating.
     */
    val popUpToRoute: String? = null,

    /**
     * Whether to include popUpToRoute in the pop operation.
     */
    val popUpToInclusive: Boolean = false
)
