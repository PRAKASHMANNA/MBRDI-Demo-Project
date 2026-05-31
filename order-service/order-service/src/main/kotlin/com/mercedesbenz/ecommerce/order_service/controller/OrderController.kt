package com.mercedesbenz.ecommerce.order_service.controller


import com.mercedesbenz.ecommerce.order_service.dto.ApiResponse
import com.mercedesbenz.ecommerce.order_service.dto.OrderDto
import com.mercedesbenz.ecommerce.order_service.dto.OrderRequestDto
import com.mercedesbenz.ecommerce.order_service.dto.OrderStatus
import com.mercedesbenz.ecommerce.order_service.service.OrderService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Order Service", description = "APIs for managing orders")
class OrderController(
    private val orderService: OrderService
) {

    private val logger = LoggerFactory.getLogger(OrderController::class.java)

    @PostMapping
    @Operation(summary = "Place a new order")
    fun placeOrder(
        @Valid @RequestBody request: OrderRequestDto
    ): ResponseEntity<ApiResponse<OrderDto>> {
        logger.info("POST /api/v1/orders")
        val order = orderService.placeOrder(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(order, "Order placed successfully"))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    fun getOrderById(@PathVariable id: Long): ResponseEntity<ApiResponse<OrderDto>> {
        logger.info("GET /api/v1/orders/{}", id)
        val order = orderService.getOrderById(id)
        return ResponseEntity.ok(ApiResponse.success(order))
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get orders by user ID")
    fun getOrdersByUserId(@PathVariable userId: Long): ResponseEntity<ApiResponse<List<OrderDto>>> {
        logger.info("GET /api/v1/orders/user/{}", userId)
        val orders = orderService.getOrdersByUserId(userId)
        return ResponseEntity.ok(ApiResponse.success(orders))
    }

    @GetMapping
    @Operation(summary = "Get all orders")
    fun getAllOrders(): ResponseEntity<ApiResponse<List<OrderDto>>> {
        logger.info("GET /api/v1/orders")
        val orders = orderService.getAllOrders()
        return ResponseEntity.ok(ApiResponse.success(orders))
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update order status")
    fun updateOrderStatus(
        @PathVariable id: Long,
        @RequestParam status: OrderStatus
    ): ResponseEntity<ApiResponse<OrderDto>> {
        logger.info("PATCH /api/v1/orders/{}/status?status={}", id, status)
        val order = orderService.updateOrderStatus(id, status)
        return ResponseEntity.ok(ApiResponse.success(order, "Order status updated"))
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel an order")
    fun cancelOrder(@PathVariable id: Long): ResponseEntity<ApiResponse<OrderDto>> {
        logger.info("PATCH /api/v1/orders/{}/cancel", id)
        val order = orderService.cancelOrder(id)
        return ResponseEntity.ok(ApiResponse.success(order, "Order cancelled successfully"))
    }
}