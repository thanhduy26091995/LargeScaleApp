package com.densitech.largescale.feature.inventory.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun InventoryScreen(modifier: Modifier = Modifier) {
    LazyRow(modifier = modifier
        .background(color = Color.Gray)
        .padding(vertical = 8.dp)) {
        items(5) {
            Text("TESTING INVENTORY SCREEN", modifier = modifier)
        }
    }
}