package com.mercedesbenz.ecommerce.order_service.event


import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderPlacedEvent(
    val orderId: Long,
    val userId: Long,
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val totalPrice: BigDecimal,
    val timestamp: LocalDateTime = LocalDateTime.now()
)