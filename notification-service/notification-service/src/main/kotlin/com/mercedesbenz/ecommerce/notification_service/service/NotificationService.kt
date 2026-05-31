package com.mercedesbenz.ecommerce.notification_service.service


import com.mercedesbenz.ecommerce.notification_service.dto.NotificationDto
import com.mercedesbenz.ecommerce.notification_service.event.OrderPlacedEvent

interface NotificationService {
    fun sendOrderNotification(event: OrderPlacedEvent): NotificationDto
    fun getNotificationsByUserId(userId: Long): List<NotificationDto>
    fun getNotificationsByOrderId(orderId: Long): List<NotificationDto>
    fun getAllNotifications(): List<NotificationDto>
}