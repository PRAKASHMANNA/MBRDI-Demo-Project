package com.mercedesbenz.ecommerce.inventory_service.event


import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderPlacedEvent(
    val orderId: Long = 0,
    val userId: Long = 0,
    val productId: Long = 0,
    val productName: String = "",
    val quantity: Int = 0,
    val totalPrice: BigDecimal = BigDecimal.ZERO,
    val timestamp: LocalDateTime = LocalDateTime.now()
)