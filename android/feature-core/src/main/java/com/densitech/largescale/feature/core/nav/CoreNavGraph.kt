package com.densitech.largescale.feature.core.nav

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.densitech.largescale.contracts.AppNavigator
import com.densitech.largescale.contracts.AuthService
import com.densitech.largescale.contracts.Routes
import com.densitech.largescale.feature.core.ui.login.LoginScreen
import com.densitech.largescale.feature.core.ui.splash.SplashScreen

/**
 * Registers core (auth) routes into the shared NavHost.
 * Called by [MainActivity] inside the NavHost builder block.
 */
fun NavGraphBuilder.coreNavGraph(
    navigator: AppNavigator,
    authService: AuthService
) {
    composable(Routes.SPLASH) {
        SplashScreen(
            authService = authService,
            onNavigate = { route -> navigator.navigateAndClearBackstack(route) }
        )
    }

    composable(Routes.LOGIN) {
        LoginScreen(
            onLoginSuccess = { navigator.navigateAndClearBackstack(Routes.HOME) }
        )
    }
}
