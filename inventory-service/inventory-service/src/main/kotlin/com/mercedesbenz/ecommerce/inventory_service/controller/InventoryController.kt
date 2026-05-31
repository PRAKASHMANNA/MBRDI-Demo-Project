package com.mercedesbenz.ecommerce.inventory_service.controller


import com.mercedesbenz.ecommerce.inventory_service.dto.ApiResponse
import com.mercedesbenz.ecommerce.inventory_service.dto.InventoryDto
import com.mercedesbenz.ecommerce.inventory_service.dto.InventoryRequestDto
import com.mercedesbenz.ecommerce.inventory_service.service.InventoryService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/inventory")
@Tag(name = "Inventory Service", description = "APIs for managing inventory")
class InventoryController(
    private val inventoryService: InventoryService
) {

    private val logger = LoggerFactory.getLogger(InventoryController::class.java)

    @PostMapping
    @Operation(summary = "Add inventory for a product")
    fun addInventory(
        @Valid @RequestBody request: InventoryRequestDto
    ): ResponseEntity<ApiResponse<InventoryDto>> {
        logger.info("POST /api/v1/inventory")
        val inventory = inventoryService.addInventory(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(inventory, "Inventory added successfully"))
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get inventory by product ID")
    fun getInventoryByProductId(
        @PathVariable productId: Long
    ): ResponseEntity<ApiResponse<InventoryDto>> {
        logger.info("GET /api/v1/inventory/product/{}", productId)
        val inventory = inventoryService.getInventoryByProductId(productId)
        return ResponseEntity.ok(ApiResponse.success(inventory))
    }

    @GetMapping
    @Operation(summary = "Get all inventory")
    fun getAllInventory(): ResponseEntity<ApiResponse<List<InventoryDto>>> {
        logger.info("GET /api/v1/inventory")
        val inventory = inventoryService.getAllInventory()
        return ResponseEntity.ok(ApiResponse.success(inventory))
    }

    @PatchMapping("/product/{productId}/stock")
    @Operation(summary = "Update stock for a product")
    fun updateStock(
        @PathVariable productId: Long,
        @RequestParam quantity: Int
    ): ResponseEntity<ApiResponse<InventoryDto>> {
        logger.info("PATCH /api/v1/inventory/product/{}/stock?quantity={}", productId, quantity)
        val inventory = inventoryService.updateStock(productId, quantity)
        return ResponseEntity.ok(ApiResponse.success(inventory, "Stock updated successfully"))
    }
}