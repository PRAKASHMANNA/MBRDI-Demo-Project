package com.mercedesbenz.ecommerce.common_lib.events


import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderPlacedEvent(
    val orderId: Long,
    val userId: Long,
    val productId: Long,
    val quantity: Int,
    val totalPrice: BigDecimal,
    val userEmail: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)