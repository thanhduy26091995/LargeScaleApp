package com.densitech.largescale

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.LaunchedEffect
import com.densitech.largescale.contracts.AuthService
import com.densitech.largescale.contracts.Routes
import com.densitech.largescale.feature.core.nav.coreNavGraph
import com.densitech.largescale.feature.dashboard.nav.dashboardNavGraph
import com.densitech.largescale.feature.inventory.nav.inventoryNavGraph
import com.densitech.largescale.feature.orders.nav.ordersNavGraph
import com.densitech.largescale.feature.wallet.nav.walletNavGraph
import com.densitech.largescale.shared.ui.theme.AppTheme
import com.densitech.largescale.wire.AppNavigatorImpl
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var navigator: AppNavigatorImpl
    @Inject lateinit var authService: AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            LaunchedEffect(navController) {
                navigator.setNavController(navController)
            }

            AppTheme {
                NavHost(
                    navController = navController,
                    startDestination = Routes.SPLASH
                ) {
                    coreNavGraph(navigator = navigator, authService = authService)
                    dashboardNavGraph(navigator = navigator)
                    ordersNavGraph(navigator = navigator)
                    inventoryNavGraph(navigator = navigator)
                    walletNavGraph(navigator = navigator)
                }
            }
        }
    }
}
