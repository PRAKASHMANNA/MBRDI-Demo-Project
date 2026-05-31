package com.mercedesbenz.ecommerce.order_service.service

import com.mercedesbenz.ecommerce.order_service.client.ProductClient
import com.mercedesbenz.ecommerce.order_service.dto.ApiResponse
import com.mercedesbenz.ecommerce.order_service.dto.OrderRequestDto
import com.mercedesbenz.ecommerce.order_service.dto.OrderStatus
import com.mercedesbenz.ecommerce.order_service.dto.ProductDto
import com.mercedesbenz.ecommerce.order_service.entity.Order
import com.mercedesbenz.ecommerce.order_service.event.OrderPlacedEvent
import com.mercedesbenz.ecommerce.order_service.exception.BadRequestException
import com.mercedesbenz.ecommerce.order_service.exception.ResourceNotFoundException
import com.mercedesbenz.ecommerce.order_service.repository.OrderRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import java.math.BigDecimal
import java.util.Optional
import java.util.concurrent.CompletableFuture

class OrderServiceTest {

    private val orderRepository: OrderRepository = mock()
    private val productClient: ProductClient = mock()
    private val kafkaTemplate: KafkaTemplate<String, OrderPlacedEvent> = mock()

    private val orderService: OrderService = OrderServiceImpl(
        orderRepository,
        productClient,
        kafkaTemplate
    )

    private fun mockProduct(stock: Int = 10): ProductDto {
        return ProductDto(
            id = 1L,
            name = "Mercedes C-Class",
            price = BigDecimal("45000.00"),
            stock = stock,
            category = "Sedan"
        )
    }

    private fun mockOrder(status: OrderStatus = OrderStatus.PENDING): Order {
        return Order(
            id = 1L,
            userId = 1L,
            productId = 1L,
            productName = "Mercedes C-Class",
            quantity = 2,
            totalPrice = BigDecimal("90000.00"),
            status = status
        )
    }

    private fun mockRequest(): OrderRequestDto {
        return OrderRequestDto(
            userId = 1L,
            productId = 1L,
            quantity = 2
        )
    }

    @Test
    fun `placeOrder - success`() {
        val product = mockProduct()
        val order = mockOrder()
        val request = mockRequest()

        whenever(productClient.getProductById(1L))
            .thenReturn(ApiResponse.success(product))

        doReturn(order)
            .whenever(orderRepository)
            .save(any<Order>())

        val sendResult: SendResult<String, OrderPlacedEvent> = mock()

        doReturn(CompletableFuture.completedFuture(sendResult))
            .whenever(kafkaTemplate)
            .send(any<String>(), any<String>(), any<OrderPlacedEvent>())

        val result = orderService.placeOrder(request)

        assertEquals(order.userId, result.userId)
        assertEquals(order.productId, result.productId)
        assertEquals(OrderStatus.PENDING, result.status)

        verify(productClient, times(1)).getProductById(1L)
        verify(orderRepository, times(1)).save(any<Order>())
        verify(kafkaTemplate, times(1))
            .send(any<String>(), any<String>(), any<OrderPlacedEvent>())
    }

    @Test
    fun `placeOrder - throws BadRequestException when insufficient stock`() {
        val lowStockProduct = mockProduct(stock = 1)
        val request = mockRequest()

        whenever(productClient.getProductById(1L))
            .thenReturn(ApiResponse.success(lowStockProduct))

        assertThrows<BadRequestException> {
            orderService.placeOrder(request)
        }

        verify(productClient, times(1)).getProductById(1L)
        verify(orderRepository, never()).save(any<Order>())
        verify(kafkaTemplate, never())
            .send(any<String>(), any<String>(), any<OrderPlacedEvent>())
    }

    @Test
    fun `placeOrder - throws ResourceNotFoundException when product not found`() {
        val request = mockRequest()

        whenever(productClient.getProductById(1L))
            .thenReturn(
                ApiResponse(
                    success = false,
                    message = "Not found",
                    data = null
                )
            )

        assertThrows<ResourceNotFoundException> {
            orderService.placeOrder(request)
        }

        verify(productClient, times(1)).getProductById(1L)
        verify(orderRepository, never()).save(any<Order>())
    }

    @Test
    fun `getOrderById - success`() {
        val order = mockOrder()

        whenever(orderRepository.findById(1L))
            .thenReturn(Optional.of(order))

        val result = orderService.getOrderById(1L)

        assertEquals(1L, result.id)
        assertEquals(order.productName, result.productName)

        verify(orderRepository, times(1)).findById(1L)
    }

    @Test
    fun `getOrderById - throws ResourceNotFoundException`() {
        whenever(orderRepository.findById(99L))
            .thenReturn(Optional.empty())

        assertThrows<ResourceNotFoundException> {
            orderService.getOrderById(99L)
        }

        verify(orderRepository, times(1)).findById(99L)
    }

    @Test
    fun `cancelOrder - success`() {
        val order = mockOrder()

        whenever(orderRepository.findById(1L))
            .thenReturn(Optional.of(order))

        doAnswer { invocation ->
            invocation.getArgument<Order>(0)
        }.whenever(orderRepository).save(any<Order>())

        val result = orderService.cancelOrder(1L)

        assertEquals(OrderStatus.CANCELLED, result.status)

        verify(orderRepository, times(1)).findById(1L)
        verify(orderRepository, times(1)).save(any<Order>())
    }

    @Test
    fun `cancelOrder - throws BadRequestException when already delivered`() {
        val deliveredOrder = mockOrder(status = OrderStatus.DELIVERED)

        whenever(orderRepository.findById(1L))
            .thenReturn(Optional.of(deliveredOrder))

        assertThrows<BadRequestException> {
            orderService.cancelOrder(1L)
        }

        verify(orderRepository, times(1)).findById(1L)
        verify(orderRepository, never()).save(any<Order>())
    }

    @Test
    fun `updateOrderStatus - throws BadRequestException when order is cancelled`() {
        val cancelledOrder = mockOrder(status = OrderStatus.CANCELLED)

        whenever(orderRepository.findById(1L))
            .thenReturn(Optional.of(cancelledOrder))

        assertThrows<BadRequestException> {
            orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED)
        }

        verify(orderRepository, times(1)).findById(1L)
        verify(orderRepository, never()).save(any<Order>())
    }
}