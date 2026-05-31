package com.mercedesbenz.ecommerce.notification_service.repository


import com.mercedesbenz.ecommerce.notification_service.entity.Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationRepository : JpaRepository<Notification, Long> {
    fun findByUserId(userId: Long): List<Notification>
    fun findByOrderId(orderId: Long): List<Notification>
}