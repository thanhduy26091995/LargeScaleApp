package com.densitech.largescale.feature.orders.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.densitech.largescale.feature.orders.data.OrderRepository
import com.densitech.largescale.feature.orders.domain.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class OrdersUiState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class OrdersViewModel @Inject constructor(
    repository: OrderRepository
) : ViewModel() {

    val uiState: StateFlow<OrdersUiState> = repository.orders.map { list ->
        OrdersUiState(orders = list.sortedByDescending { it.createdAt }, isLoading = false)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = OrdersUiState()
    )
}
