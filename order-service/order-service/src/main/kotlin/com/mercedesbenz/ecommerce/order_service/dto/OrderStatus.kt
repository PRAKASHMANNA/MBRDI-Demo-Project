package com.mercedesbenz.ecommerce.order_service.dto


enum class OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}