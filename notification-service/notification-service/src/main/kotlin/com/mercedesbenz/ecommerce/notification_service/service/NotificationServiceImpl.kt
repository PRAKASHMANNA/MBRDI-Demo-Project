package com.mercedesbenz.ecommerce.notification_service.service


import com.mercedesbenz.ecommerce.notification_service.dto.NotificationDto
import com.mercedesbenz.ecommerce.notification_service.dto.NotificationStatus
import com.mercedesbenz.ecommerce.notification_service.dto.NotificationType
import com.mercedesbenz.ecommerce.notification_service.entity.Notification
import com.mercedesbenz.ecommerce.notification_service.event.OrderPlacedEvent
import com.mercedesbenz.ecommerce.notification_service.exception.ResourceNotFoundException
import com.mercedesbenz.ecommerce.notification_service.repository.NotificationRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class NotificationServiceImpl(
    private val notificationRepository: NotificationRepository
) : NotificationService {

    private val logger = LoggerFactory.getLogger(NotificationServiceImpl::class.java)

    override fun sendOrderNotification(event: OrderPlacedEvent): NotificationDto {
        logger.info(
            "Sending notification for orderId: {}, userId: {}",
            event.orderId, event.userId
        )

        val message = buildMessage(event)

        val notification = Notification(
            orderId = event.orderId,
            userId = event.userId,
            message = message,
            type = NotificationType.ORDER_PLACED,
            status = NotificationStatus.SENT
        )

        val saved = notificationRepository.save(notification)

        logger.info(
            "Notification sent successfully for orderId: {}, notificationId: {}",
            event.orderId, saved.id
        )

        return saved.toDto()
    }

    override fun getNotificationsByUserId(userId: Long): List<NotificationDto> {
        logger.info("Fetching notifications for userId: {}", userId)
        return notificationRepository.findByUserId(userId).map { it.toDto() }
    }

    override fun getNotificationsByOrderId(orderId: Long): List<NotificationDto> {
        logger.info("Fetching notifications for orderId: {}", orderId)
        val notifications = notificationRepository.findByOrderId(orderId)
        if (notifications.isEmpty()) {
            throw ResourceNotFoundException("No notifications found for orderId: $orderId")
        }
        return notifications.map { it.toDto() }
    }

    override fun getAllNotifications(): List<NotificationDto> {
        logger.info("Fetching all notifications")
        return notificationRepository.findAll().map { it.toDto() }
    }

    private fun buildMessage(event: OrderPlacedEvent): String =
        "Dear Customer (userId: ${event.userId}), " +
                "your order #${event.orderId} for '${event.productName}' " +
                "(qty: ${event.quantity}) worth ₹${event.totalPrice} " +
                "has been placed successfully!"

    private fun Notification.toDto() = NotificationDto(
        id = this.id,
        orderId = this.orderId,
        userId = this.userId,
        message = this.message,
        type = this.type,
        status = this.status,
        createdAt = this.createdAt
    )
}