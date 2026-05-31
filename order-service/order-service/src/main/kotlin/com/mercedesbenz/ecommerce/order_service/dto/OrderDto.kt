package com.mercedesbenz.ecommerce.order_service.dto


import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderDto(
    val id: Long? = null,
    val userId: Long,
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val totalPrice: BigDecimal,
    val status: OrderStatus,
    val createdAt: LocalDateTime? = null
)