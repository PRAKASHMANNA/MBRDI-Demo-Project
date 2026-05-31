package com.mercedesbenz.ecommerce.order_service.service


import com.mercedesbenz.ecommerce.order_service.dto.OrderDto
import com.mercedesbenz.ecommerce.order_service.dto.OrderRequestDto
import com.mercedesbenz.ecommerce.order_service.dto.OrderStatus

interface OrderService {
    fun placeOrder(request: OrderRequestDto): OrderDto
    fun getOrderById(id: Long): OrderDto
    fun getOrdersByUserId(userId: Long): List<OrderDto>
    fun getAllOrders(): List<OrderDto>
    fun updateOrderStatus(id: Long, status: OrderStatus): OrderDto
    fun cancelOrder(id: Long): OrderDto
}