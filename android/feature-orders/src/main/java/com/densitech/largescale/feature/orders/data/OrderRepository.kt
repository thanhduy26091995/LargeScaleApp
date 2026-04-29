package com.densitech.largescale.feature.orders.data

import com.densitech.largescale.feature.orders.domain.Order
import com.densitech.largescale.feature.orders.domain.OrderItem
import com.densitech.largescale.feature.orders.domain.OrderStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor() {

    private val _orders = MutableStateFlow(mockOrders())
    val orders: Flow<List<Order>> = _orders.asStateFlow()

    fun getOrderById(id: String): Flow<Order?> =
        _orders.map { list -> list.find { it.id == id } }

    suspend fun createOrder(order: Order) {
        delay(300) // simulate network
        _orders.value = _orders.value + order
    }

    fun getRecentOrders(limit: Int = 3): Flow<List<Order>> =
        _orders.map { it.sortedByDescending { o -> o.createdAt }.take(limit) }

    private fun mockOrders() = listOf(
        Order(
            id = "ORD-001",
            customerName = "Alice Johnson",
            items = listOf(
                OrderItem("Widget A", 2, 24.99),
                OrderItem("Widget B", 1, 14.99)
            ),
            status = OrderStatus.DELIVERED,
            createdAt = System.currentTimeMillis() - 86_400_000L * 3
        ),
        Order(
            id = "ORD-002",
            customerName = "Bob Smith",
            items = listOf(OrderItem("Gadget X", 1, 89.99)),
            status = OrderStatus.SHIPPED,
            createdAt = System.currentTimeMillis() - 86_400_000L
        ),
        Order(
            id = "ORD-003",
            customerName = "Carol White",
            items = listOf(
                OrderItem("Part Y", 5, 9.99),
                OrderItem("Part Z", 3, 4.99)
            ),
            status = OrderStatus.PENDING,
            createdAt = System.currentTimeMillis() - 3_600_000L
        )
    )
}
