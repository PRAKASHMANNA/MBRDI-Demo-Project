package com.mercedesbenz.ecommerce.order_service.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class OrderRequestDto(

    @field:NotNull(message = "User ID is required")
    val userId: Long,

    @field:NotNull(message = "Product ID is required")
    val productId: Long,

    @field:Min(value = 1, message = "Quantity must be at least 1")
    val quantity: Int
)