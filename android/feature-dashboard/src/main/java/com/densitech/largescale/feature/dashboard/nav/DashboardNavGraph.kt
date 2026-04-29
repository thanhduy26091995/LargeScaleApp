package com.densitech.largescale.feature.dashboard.nav

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.densitech.largescale.contracts.AppNavigator
import com.densitech.largescale.contracts.Routes
import com.densitech.largescale.feature.dashboard.ui.home.HomeScreen

fun NavGraphBuilder.dashboardNavGraph(navigator: AppNavigator) {
    composable(Routes.HOME) {
        HomeScreen(
            onLogout = { navigator.navigateAndClearBackstack(Routes.LOGIN) }
        )
    }
}
