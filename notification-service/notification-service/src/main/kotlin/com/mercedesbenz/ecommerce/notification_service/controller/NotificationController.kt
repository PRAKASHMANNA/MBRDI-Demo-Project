package com.mercedesbenz.ecommerce.notification_service.controller


import com.mercedesbenz.ecommerce.notification_service.dto.ApiResponse
import com.mercedesbenz.ecommerce.notification_service.dto.NotificationDto
import com.mercedesbenz.ecommerce.notification_service.service.NotificationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notification Service", description = "APIs for managing notifications")
class NotificationController(
    private val notificationService: NotificationService
) {

    private val logger = LoggerFactory.getLogger(NotificationController::class.java)

    @GetMapping
    @Operation(summary = "Get all notifications")
    fun getAllNotifications(): ResponseEntity<ApiResponse<List<NotificationDto>>> {
        logger.info("GET /api/v1/notifications")
        val notifications = notificationService.getAllNotifications()
        return ResponseEntity.ok(ApiResponse.success(notifications))
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get notifications by user ID")
    fun getNotificationsByUserId(
        @PathVariable userId: Long
    ): ResponseEntity<ApiResponse<List<NotificationDto>>> {
        logger.info("GET /api/v1/notifications/user/{}", userId)
        val notifications = notificationService.getNotificationsByUserId(userId)
        return ResponseEntity.ok(ApiResponse.success(notifications))
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get notifications by order ID")
    fun getNotificationsByOrderId(
        @PathVariable orderId: Long
    ): ResponseEntity<ApiResponse<List<NotificationDto>>> {
        logger.info("GET /api/v1/notifications/order/{}", orderId)
        val notifications = notificationService.getNotificationsByOrderId(orderId)
        return ResponseEntity.ok(ApiResponse.success(notifications))
    }
}