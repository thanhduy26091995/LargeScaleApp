package com.densitech.largescale.feature.orders.domain

data class Order(
    val id: String,
    val customerName: String,
    val items: List<OrderItem>,
    val status: OrderStatus,
    val createdAt: Long = System.currentTimeMillis()
) {
    val total: Double get() = items.sumOf { it.price * it.quantity }
    val itemCount: Int get() = items.sumOf { it.quantity }
}

data class OrderItem(
    val name: String,
    val quantity: Int,
    val price: Double
)

enum class OrderStatus { PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED }
