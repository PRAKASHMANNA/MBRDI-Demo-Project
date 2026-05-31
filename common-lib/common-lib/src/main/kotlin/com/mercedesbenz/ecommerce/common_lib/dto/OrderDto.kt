package com.mercedesbenz.ecommerce.common_lib.dto


import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderDto(
    val id: Long? = null,
    val userId: Long,
    val productId: Long,
    val quantity: Int,
    val totalPrice: BigDecimal,
    val status: OrderStatus = OrderStatus.PENDING,
    val createdAt: LocalDateTime? = null
)

data class OrderRequestDto(
    val userId: Long,
    val productId: Long,
    val quantity: Int
)

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}