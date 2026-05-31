package com.mercedesbenz.ecommerce.notification_service.dto


import java.time.LocalDateTime

data class NotificationDto(
    val id: Long? = null,
    val orderId: Long,
    val userId: Long,
    val message: String,
    val type: NotificationType,
    val status: NotificationStatus,
    val createdAt: LocalDateTime? = null
)

enum class NotificationType {
    ORDER_PLACED,
    ORDER_CONFIRMED,
    ORDER_SHIPPED,
    ORDER_DELIVERED,
    ORDER_CANCELLED
}

enum class NotificationStatus {
    SENT,
    FAILED,
    PENDING
}