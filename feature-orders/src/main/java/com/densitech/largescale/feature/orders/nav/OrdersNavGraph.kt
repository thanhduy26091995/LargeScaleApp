package com.densitech.largescale.feature.orders.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.densitech.largescale.contracts.AppNavigator
import com.densitech.largescale.contracts.Routes
import com.densitech.largescale.feature.orders.ui.list.OrdersListScreen

fun NavGraphBuilder.ordersNavGraph(navigator: AppNavigator) {
    composable(Routes.ORDERS) {
        OrdersListScreen(
            onOrderClick = { orderId ->
                navigator.navigate(Routes.buildRoute(Routes.ORDER_DETAIL, "orderId" to orderId))
            },
            onBack = { navigator.navigateBack() }
        )
    }

    composable(Routes.ORDER_DETAIL) { backStackEntry ->
        val orderId = backStackEntry.arguments?.getString("orderId") ?: return@composable
        // Phase 7: full detail screen — placeholder for now
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Order Detail: $orderId")
        }
    }
}
