package com.mercedesbenz.ecommerce.inventory_service.dto


import java.time.LocalDateTime

data class InventoryDto(
    val id: Long? = null,
    val productId: Long,
    val productName: String,
    val availableStock: Int,
    val reservedStock: Int,
    val updatedAt: LocalDateTime? = null
)