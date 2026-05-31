package com.mercedesbenz.ecommerce.inventory_service.service


import com.mercedesbenz.ecommerce.inventory_service.dto.InventoryDto
import com.mercedesbenz.ecommerce.inventory_service.dto.InventoryRequestDto
import com.mercedesbenz.ecommerce.inventory_service.entity.Inventory
import com.mercedesbenz.ecommerce.inventory_service.exception.BadRequestException
import com.mercedesbenz.ecommerce.inventory_service.exception.ResourceNotFoundException
import com.mercedesbenz.ecommerce.inventory_service.repository.InventoryRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class InventoryServiceImpl(
    private val inventoryRepository: InventoryRepository
) : InventoryService {

    private val logger = LoggerFactory.getLogger(InventoryServiceImpl::class.java)

    override fun addInventory(request: InventoryRequestDto): InventoryDto {
        logger.info("Adding inventory for productId: {}", request.productId)

        if (inventoryRepository.existsByProductId(request.productId)) {
            throw BadRequestException("Inventory already exists for productId: ${request.productId}")
        }

        val inventory = Inventory(
            productId = request.productId,
            productName = request.productName,
            availableStock = request.availableStock
        )

        val saved = inventoryRepository.save(inventory)
        logger.info("Inventory added for productId: {}", saved.productId)
        return saved.toDto()
    }

    override fun getInventoryByProductId(productId: Long): InventoryDto {
        logger.info("Fetching inventory for productId: {}", productId)
        val inventory = inventoryRepository.findByProductId(productId)
            .orElseThrow { ResourceNotFoundException("Inventory not found for productId: $productId") }
        return inventory.toDto()
    }

    override fun getAllInventory(): List<InventoryDto> {
        logger.info("Fetching all inventory")
        return inventoryRepository.findAll().map { it.toDto() }
    }

    override fun reduceStock(productId: Long, quantity: Int) {
        logger.info("Reducing stock for productId: {}, quantity: {}", productId, quantity)

        val inventory = inventoryRepository.findByProductId(productId)
            .orElseThrow { ResourceNotFoundException("Inventory not found for productId: $productId") }

        if (inventory.availableStock < quantity) {
            throw BadRequestException(
                "Insufficient stock for productId: $productId. " +
                        "Available: ${inventory.availableStock}, Requested: $quantity"
            )
        }

        inventory.availableStock -= quantity
        inventory.reservedStock += quantity
        inventory.updatedAt = LocalDateTime.now()

        inventoryRepository.save(inventory)
        logger.info("Stock reduced for productId: {}, remaining: {}", productId, inventory.availableStock)
    }

    override fun updateStock(productId: Long, quantity: Int): InventoryDto {
        logger.info("Updating stock for productId: {}, quantity: {}", productId, quantity)

        val inventory = inventoryRepository.findByProductId(productId)
            .orElseThrow { ResourceNotFoundException("Inventory not found for productId: $productId") }

        if (inventory.availableStock + quantity < 0) {
            throw BadRequestException("Stock cannot go below zero for productId: $productId")
        }

        inventory.availableStock += quantity
        inventory.updatedAt = LocalDateTime.now()

        val saved = inventoryRepository.save(inventory)
        logger.info("Stock updated for productId: {}, new stock: {}", productId, saved.availableStock)
        return saved.toDto()
    }

    private fun Inventory.toDto() = InventoryDto(
        id = this.id,
        productId = this.productId,
        productName = this.productName,
        availableStock = this.availableStock,
        reservedStock = this.reservedStock,
        updatedAt = this.updatedAt
    )
}