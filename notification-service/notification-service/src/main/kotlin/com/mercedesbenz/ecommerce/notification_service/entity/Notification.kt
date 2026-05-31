package com.mercedesbenz.ecommerce.notification_service.entity


import com.mercedesbenz.ecommerce.notification_service.dto.NotificationStatus
import com.mercedesbenz.ecommerce.notification_service.dto.NotificationType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "notifications")
class Notification(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false)
    var orderId: Long,

    @Column(nullable = false)
    var userId: Long,

    @Column(nullable = false, columnDefinition = "TEXT")
    var message: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: NotificationType,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: NotificationStatus = NotificationStatus.PENDING,

    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)