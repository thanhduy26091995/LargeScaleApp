package com.densitech.largescale.wire

import android.util.Log
import androidx.navigation.NavHostController
import com.densitech.largescale.contracts.AppNavigator
import com.densitech.largescale.contracts.NavigationArgs
import com.densitech.largescale.contracts.NavigationOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "AppNavigatorImpl"

/**
 * Compose Navigation implementation of [AppNavigator].
 *
 * Wraps [NavHostController] created in [MainActivity] via `rememberNavController()`.
 * The controller is injected post-composition via [setNavController].
 *
 * This is a singleton so all modules share the same navigation state.
 * [MainActivity] must call [setNavController] once the Composable tree is ready.
 */
@Singleton
class AppNavigatorImpl @Inject constructor() : AppNavigator {

    private var navController: NavHostController? = null
    private val registeredRoutes = mutableSetOf<String>()

    private val _currentRoute = MutableStateFlow<String?>(null)
    override val currentRoute: StateFlow<String?> = _currentRoute.asStateFlow()

    // ── Setup ─────────────────────────────────────────────────────────────────

    /**
     * Attach the [NavHostController] created inside the Compose tree.
     * Called once from [MainActivity] via a [androidx.compose.runtime.LaunchedEffect].
     */
    fun setNavController(controller: NavHostController) {
        navController = controller
        controller.addOnDestinationChangedListener { _, destination, _ ->
            _currentRoute.value = destination.route
            Log.d(TAG, "Navigated to: ${destination.route}")
        }
    }

    /**
     * Register a route pattern as known. Called by [NavigationAssembler] when building
     * the [NavHost] from module-contributed routes.
     */
    fun registerRoute(route: String) {
        registeredRoutes.add(route)
    }

    // ── AppNavigator ──────────────────────────────────────────────────────────

    override fun navigate(route: String, args: NavigationArgs, options: NavigationOptions) {
        val controller = navController ?: run {
            Log.w(TAG, "navigate('$route') called before NavController was set — ignored")
            return
        }
        val resolvedRoute = buildRouteWithArgs(route, args)
        controller.navigate(resolvedRoute) {
            if (options.clearBackstack) {
                popUpTo(0) { inclusive = true }
            }
            options.popUpToRoute?.let { popTo ->
                popUpTo(popTo) { inclusive = options.popUpToInclusive }
            }
            launchSingleTop = options.singleTop
        }
    }

    override fun navigateBack(): Boolean {
        return navController?.popBackStack() ?: false
    }

    override fun navigateUp() {
        navController?.navigateUp()
    }

    override fun navigateAndClearBackstack(route: String) {
        val controller = navController ?: return
        controller.navigate(route) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

    override fun isRouteRegistered(route: String): Boolean = route in registeredRoutes

    override fun getBackstackDepth(): Int = navController?.currentBackStack?.value?.size ?: 0

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Append string query parameters to a route.
     * Path parameters ({param}) should already be substituted via [NavigationArgs.stringArgs]
     * before calling this — this method only handles extra query-string args.
     */
    private fun buildRouteWithArgs(route: String, args: NavigationArgs): String {
        if (args.stringArgs.isEmpty() && args.intArgs.isEmpty() && args.boolArgs.isEmpty()) {
            return route
        }
        val params = buildList {
            args.stringArgs.forEach { (k, v) -> add("$k=$v") }
            args.intArgs.forEach { (k, v) -> add("$k=$v") }
            args.boolArgs.forEach { (k, v) -> add("$k=$v") }
        }.joinToString("&")

        return if (route.contains("?")) "$route&$params" else "$route?$params"
    }
}
