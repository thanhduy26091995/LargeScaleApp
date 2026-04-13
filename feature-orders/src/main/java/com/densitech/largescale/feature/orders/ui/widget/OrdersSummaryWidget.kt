package com.densitech.largescale.feature.orders.ui.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.densitech.largescale.feature.orders.data.OrderRepository
import com.densitech.largescale.feature.orders.domain.Order
import com.densitech.largescale.shared.ui.components.AppCard
import com.densitech.largescale.shared.ui.theme.Spacing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class OrdersWidgetViewModel @Inject constructor(
    repository: OrderRepository
) : ViewModel() {
    val recentOrders: StateFlow<List<Order>> = repository.getRecentOrders(limit = 3).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )
}

/**
 * Dashboard home widget for the orders module.
 * Registered into [SlotIds.HOME_WIDGETS] by [OrdersFeatureModule].
 */
@Composable
fun OrdersSummaryWidget(
    modifier: Modifier = Modifier,
    viewModel: OrdersWidgetViewModel = hiltViewModel()
) {
    val orders by viewModel.recentOrders.collectAsState()

    AppCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Recent Orders", style = MaterialTheme.typography.titleSmall)
            Text(
                "${orders.size} orders",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        orders.forEach { order ->
            Row(
                modifier = Modifier.fillMaxWidth().also { },
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(order.id, style = MaterialTheme.typography.bodySmall)
                Text(
                    order.status.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (orders.isEmpty()) {
            Text(
                "No orders yet",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
