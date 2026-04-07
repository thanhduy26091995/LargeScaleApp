package com.densitech.largescale

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.densitech.largescale.contracts.Routes
import com.densitech.largescale.ui.theme.LargeScaleModuleTheme
import com.densitech.largescale.wire.AppNavigatorImpl
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Single Activity — hosts the entire navigation graph.
 *
 * Responsibilities:
 * 1. Create [NavHostController] and hand it to [AppNavigatorImpl]
 * 2. Build the [NavHost] with routes contributed by feature modules
 *
 * Phase 6: The [NavHost] will be driven by [NavigationAssembler] which collects
 * routes from all registered modules. For now, a placeholder splash screen is shown
 * to confirm Wire Core is wired up correctly.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var navigator: AppNavigatorImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            // Connect the Compose NavController to Wire Core's AppNavigator
            LaunchedEffect(navController) {
                navigator.setNavController(navController)
            }

            LargeScaleModuleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Phase 6: Replace NavHost content with NavigationAssembler routes
                    NavHost(
                        navController = navController,
                        startDestination = Routes.SPLASH,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Routes.SPLASH) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Wire Core Ready\nFeature modules will load here",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
