package com.densitech.largescale.contracts

/**
 * Standard navigation routes used across the application.
 * Feature modules should extend this with their own route constants.
 *
 * Route naming convention:
 * - Use lowercase with forward slashes
 * - Parameters in curly braces: "/orders/{orderId}"
 * - Query params via NavigationArgs
 */
object Routes {
    // Core routes (always available)
    const val SPLASH = "/splash"
    const val LOGIN = "/login"
    const val REGISTER = "/register"
    const val SETTINGS = "/settings"
    
    // Dashboard routes
    const val HOME = "/home"
    const val PROFILE = "/profile"
    
    // Orders routes (example)
    const val ORDERS = "/orders"
    const val ORDER_DETAIL = "/orders/{orderId}"
    const val ORDER_CREATE = "/orders/create"
    
    // Inventory routes (example)
    const val INVENTORY = "/inventory"
    const val INVENTORY_DETAIL = "/inventory/{itemId}"
    
    // Wallet routes (example)
    const val WALLET = "/wallet"
    const val WALLET_TRANSACTION = "/wallet/transaction/{transactionId}"
    
    /**
     * Helper to build a route with parameters.
     *
     * Example:
     * ```
     * val route = Routes.buildRoute(Routes.ORDER_DETAIL, "orderId" to "12345")
     * // Result: "/orders/12345"
     * ```
     */
    fun buildRoute(template: String, vararg params: Pair<String, String>): String {
        var result = template
        params.forEach { (key, value) ->
            result = result.replace("{$key}", value)
        }
        return result
    }
}

/**
 * Sealed class for type-safe navigation destinations.
 * Alternative to string-based routes for increased compile-time safety.
 *
 * Usage:
 * ```
 * navigator.navigate(NavigationDestination.OrderDetail(orderId = "123"))
 * ```
 */
sealed class NavigationDestination {
    abstract fun toRoute(): String

    // Core destinations
    object Splash : NavigationDestination() {
        override fun toRoute() = Routes.SPLASH
    }

    object Login : NavigationDestination() {
        override fun toRoute() = Routes.LOGIN
    }

    object Home : NavigationDestination() {
        override fun toRoute() = Routes.HOME
    }

    // Parametrized destinations
    data class OrderDetail(val orderId: String) : NavigationDestination() {
        override fun toRoute() = Routes.buildRoute(Routes.ORDER_DETAIL, "orderId" to orderId)
    }

    data class InventoryDetail(val itemId: String) : NavigationDestination() {
        override fun toRoute() = Routes.buildRoute(Routes.INVENTORY_DETAIL, "itemId" to itemId)
    }

    data class WalletTransaction(val transactionId: String) : NavigationDestination() {
        override fun toRoute() = Routes.buildRoute(Routes.WALLET_TRANSACTION, "transactionId" to transactionId)
    }
}

/**
 * Extension function to navigate using NavigationDestination.
 */
fun AppNavigator.navigate(
    destination: NavigationDestination,
    options: NavigationOptions = NavigationOptions()
) {
    navigate(destination.toRoute(), options = options)
}
