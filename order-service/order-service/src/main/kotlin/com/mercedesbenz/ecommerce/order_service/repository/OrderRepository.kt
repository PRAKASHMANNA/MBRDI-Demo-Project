package com.mercedesbenz.ecommerce.order_service.repository

import com.mercedesbenz.ecommerce.order_service.dto.OrderStatus
import com.mercedesbenz.ecommerce.order_service.entity.Order
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository : JpaRepository<Order, Long> {
    fun findByUserId(userId: Long): List<Order>
    fun findByStatus(status: OrderStatus): List<Order>
    fun findByUserIdAndStatus(userId: Long, status: OrderStatus): List<Order>
}