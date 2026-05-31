package com.mercedesbenz.ecommerce.inventory_service.consumer


import com.mercedesbenz.ecommerce.inventory_service.event.OrderPlacedEvent
import com.mercedesbenz.ecommerce.inventory_service.service.InventoryService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class OrderEventConsumer(
    private val inventoryService: InventoryService
) {

    private val logger = LoggerFactory.getLogger(OrderEventConsumer::class.java)

    @KafkaListener(
        topics = ["order-placed"],
        groupId = "inventory-group"
    )
    fun handleOrderPlaced(event: OrderPlacedEvent) {
        logger.info(
            "Received OrderPlacedEvent - orderId: {}, productId: {}, quantity: {}",
            event.orderId, event.productId, event.quantity
        )

        try {
            inventoryService.reduceStock(event.productId, event.quantity)
            logger.info(
                "Stock reduced successfully for orderId: {}, productId: {}",
                event.orderId, event.productId
            )
        } catch (ex: Exception) {
            logger.error(
                "Failed to reduce stock for orderId: {}, productId: {}. Error: {}",
                event.orderId, event.productId, ex.message
            )
        }
    }
}