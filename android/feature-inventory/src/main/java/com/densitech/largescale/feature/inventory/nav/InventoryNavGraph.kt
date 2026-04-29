package com.densitech.largescale.feature.inventory.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.densitech.largescale.contracts.AppNavigator
import com.densitech.largescale.contracts.Routes

fun NavGraphBuilder.inventoryNavGraph(navigator: AppNavigator) {
    composable(Routes.INVENTORY) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Inventory — coming soon")
        }
    }
}
