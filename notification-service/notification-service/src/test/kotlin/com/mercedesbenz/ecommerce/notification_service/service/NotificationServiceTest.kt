package com.mercedesbenz.ecommerce.notification_service.service

import com.mercedesbenz.ecommerce.notification_service.dto.NotificationStatus
import com.mercedesbenz.ecommerce.notification_service.dto.NotificationType
import com.mercedesbenz.ecommerce.notification_service.entity.Notification
import com.mercedesbenz.ecommerce.notification_service.event.OrderPlacedEvent
import com.mercedesbenz.ecommerce.notification_service.exception.ResourceNotFoundException
import com.mercedesbenz.ecommerce.notification_service.repository.NotificationRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal

class NotificationServiceTest {

    private val notificationRepository: NotificationRepository = mock()

    private val notificationService: NotificationService =
        NotificationServiceImpl(notificationRepository)

    private val mockEvent = OrderPlacedEvent(
        orderId = 1L,
        userId = 1L,
        productId = 1L,
        productName = "Mercedes C-Class",
        quantity = 2,
        totalPrice = BigDecimal("90000.00")
    )

    private val mockNotification = Notification(
        id = 1L,
        orderId = 1L,
        userId = 1L,
        message = "Your order #1 has been placed successfully!",
        type = NotificationType.ORDER_PLACED,
        status = NotificationStatus.SENT
    )

    @Test
    fun `sendOrderNotification - success`() {
        doReturn(mockNotification)
            .whenever(notificationRepository)
            .save(any<Notification>())

        val result = notificationService.sendOrderNotification(mockEvent)

        assertEquals(mockNotification.orderId, result.orderId)
        assertEquals(mockNotification.userId, result.userId)
        assertEquals(NotificationStatus.SENT, result.status)
        assertEquals(NotificationType.ORDER_PLACED, result.type)

        verify(notificationRepository, times(1)).save(any<Notification>())
    }

    @Test
    fun `getNotificationsByUserId - returns list`() {
        whenever(notificationRepository.findByUserId(1L))
            .thenReturn(listOf(mockNotification))

        val result = notificationService.getNotificationsByUserId(1L)

        assertEquals(1, result.size)
        assertEquals(1L, result[0].userId)

        verify(notificationRepository, times(1)).findByUserId(1L)
    }

    @Test
    fun `getNotificationsByUserId - returns empty list`() {
        whenever(notificationRepository.findByUserId(99L))
            .thenReturn(emptyList())

        val result = notificationService.getNotificationsByUserId(99L)

        assertEquals(0, result.size)

        verify(notificationRepository, times(1)).findByUserId(99L)
    }

    @Test
    fun `getNotificationsByOrderId - success`() {
        whenever(notificationRepository.findByOrderId(1L))
            .thenReturn(listOf(mockNotification))

        val result = notificationService.getNotificationsByOrderId(1L)

        assertEquals(1, result.size)
        assertEquals(1L, result[0].orderId)

        verify(notificationRepository, times(1)).findByOrderId(1L)
    }

    @Test
    fun `getNotificationsByOrderId - throws ResourceNotFoundException`() {
        whenever(notificationRepository.findByOrderId(99L))
            .thenReturn(emptyList())

        assertThrows<ResourceNotFoundException> {
            notificationService.getNotificationsByOrderId(99L)
        }

        verify(notificationRepository, times(1)).findByOrderId(99L)
    }

    @Test
    fun `getAllNotifications - returns list`() {
        whenever(notificationRepository.findAll())
            .thenReturn(listOf(mockNotification))

        val result = notificationService.getAllNotifications()

        assertEquals(1, result.size)

        verify(notificationRepository, times(1)).findAll()
    }
}