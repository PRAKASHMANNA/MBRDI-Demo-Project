package com.mercedesbenz.ecommerce.notification_service.consumer


import com.mercedesbenz.ecommerce.notification_service.event.OrderPlacedEvent
import com.mercedesbenz.ecommerce.notification_service.service.NotificationService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class OrderEventConsumer(
    private val notificationService: NotificationService
) {

    private val logger = LoggerFactory.getLogger(OrderEventConsumer::class.java)

    @KafkaListener(
        topics = ["order-placed"],
        groupId = "notification-group"
    )
    fun handleOrderPlaced(event: OrderPlacedEvent) {
        logger.info(
            "Received OrderPlacedEvent - orderId: {}, userId: {}, product: {}",
            event.orderId, event.userId, event.productName
        )

        try {
            notificationService.sendOrderNotification(event)
            logger.info(
                "Notification processed for orderId: {}",
                event.orderId
            )
        } catch (ex: Exception) {
            logger.error(
                "Failed to send notification for orderId: {}. Error: {}",
                event.orderId, ex.message
            )
        }
    }
}