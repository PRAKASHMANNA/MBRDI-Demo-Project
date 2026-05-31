package com.mercedesbenz.ecommerce.inventory_service.service


import com.mercedesbenz.ecommerce.inventory_service.dto.InventoryDto
import com.mercedesbenz.ecommerce.inventory_service.dto.InventoryRequestDto

interface InventoryService {
    fun addInventory(request: InventoryRequestDto): InventoryDto
    fun getInventoryByProductId(productId: Long): InventoryDto
    fun getAllInventory(): List<InventoryDto>
    fun reduceStock(productId: Long, quantity: Int)
    fun updateStock(productId: Long, quantity: Int): InventoryDto
}