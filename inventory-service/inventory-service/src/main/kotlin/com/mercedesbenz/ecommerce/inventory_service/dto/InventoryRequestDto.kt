package com.mercedesbenz.ecommerce.inventory_service.dto


import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class InventoryRequestDto(

    @field:NotNull(message = "Product ID is required")
    val productId: Long,

    @field:NotBlank(message = "Product name is required")
    val productName: String,

    @field:Min(value = 0, message = "Stock cannot be negative")
    val availableStock: Int
)