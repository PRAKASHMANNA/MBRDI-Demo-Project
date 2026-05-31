package com.mercedesbenz.ecommerce.order_service.service


import com.mercedesbenz.ecommerce.order_service.client.ProductClient
import com.mercedesbenz.ecommerce.order_service.dto.OrderDto
import com.mercedesbenz.ecommerce.order_service.dto.OrderRequestDto
import com.mercedesbenz.ecommerce.order_service.dto.OrderStatus
import com.mercedesbenz.ecommerce.order_service.entity.Order
import com.mercedesbenz.ecommerce.order_service.event.OrderPlacedEvent
import com.mercedesbenz.ecommerce.order_service.exception.BadRequestException
import com.mercedesbenz.ecommerce.order_service.exception.ResourceNotFoundException
import com.mercedesbenz.ecommerce.order_service.repository.OrderRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class OrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val productClient: ProductClient,
    private val kafkaTemplate: KafkaTemplate<String, OrderPlacedEvent>
) : OrderService {

    private val logger = LoggerFactory.getLogger(OrderServiceImpl::class.java)

    override fun placeOrder(request: OrderRequestDto): OrderDto {
        logger.info("Placing order for userId: {}, productId: {}", request.userId, request.productId)

        // Feign call to product-service
        val productResponse = productClient.getProductById(request.productId)
        val product = productResponse.data
            ?: throw ResourceNotFoundException("Product not found with id: ${request.productId}")

        if (product.stock < request.quantity) {
            throw BadRequestException("Insufficient stock. Available: ${product.stock}, Requested: ${request.quantity}")
        }

        val totalPrice = product.price.multiply(request.quantity.toBigDecimal())

        val order = Order(
            userId = request.userId,
            productId = request.productId,
            productName = product.name,
            quantity = request.quantity,
            totalPrice = totalPrice,
            status = OrderStatus.PENDING
        )

        val saved = orderRepository.save(order)
        logger.info("Order placed with id: {}", saved.id)

        // Kafka publish
        val event = OrderPlacedEvent(
            orderId = saved.id,
            userId = saved.userId,
            productId = saved.productId,
            productName = saved.productName,
            quantity = saved.quantity,
            totalPrice = saved.totalPrice
        )

        kafkaTemplate.send("order-placed", saved.id.toString(), event)
        logger.info("OrderPlacedEvent published for orderId: {}", saved.id)

        return saved.toDto()
    }

    override fun getOrderById(id: Long): OrderDto {
        logger.info("Fetching order by id: {}", id)
        val order = orderRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Order not found with id: $id") }
        return order.toDto()
    }

    override fun getOrdersByUserId(userId: Long): List<OrderDto> {
        logger.info("Fetching orders for userId: {}", userId)
        return orderRepository.findByUserId(userId).map { it.toDto() }
    }

    override fun getAllOrders(): List<OrderDto> {
        logger.info("Fetching all orders")
        return orderRepository.findAll().map { it.toDto() }
    }

    override fun updateOrderStatus(id: Long, status: OrderStatus): OrderDto {
        logger.info("Updating order status for id: {}, status: {}", id, status)
        val order = orderRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Order not found with id: $id") }

        if (order.status == OrderStatus.CANCELLED) {
            throw BadRequestException("Cannot update a cancelled order")
        }

        order.status = status
        order.updatedAt = LocalDateTime.now()

        val saved = orderRepository.save(order)
        logger.info("Order status updated for id: {}", saved.id)
        return saved.toDto()
    }

    override fun cancelOrder(id: Long): OrderDto {
        logger.info("Cancelling order id: {}", id)
        val order = orderRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Order not found with id: $id") }

        if (order.status == OrderStatus.DELIVERED) {
            throw BadRequestException("Cannot cancel a delivered order")
        }

        if (order.status == OrderStatus.CANCELLED) {
            throw BadRequestException("Order is already cancelled")
        }

        order.status = OrderStatus.CANCELLED
        order.updatedAt = LocalDateTime.now()

        val saved = orderRepository.save(order)
        logger.info("Order cancelled with id: {}", saved.id)
        return saved.toDto()
    }

    private fun Order.toDto() = OrderDto(
        id = this.id,
        userId = this.userId,
        productId = this.productId,
        productName = this.productName,
        quantity = this.quantity,
        totalPrice = this.totalPrice,
        status = this.status,
        createdAt = this.createdAt
    )
}