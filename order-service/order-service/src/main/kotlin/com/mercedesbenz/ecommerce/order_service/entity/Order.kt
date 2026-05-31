package com.mercedesbenz.ecommerce.order_service.entity


import com.mercedesbenz.ecommerce.order_service.dto.OrderStatus
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
class Order(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false)
    var userId: Long,

    @Column(nullable = false)
    var productId: Long,

    @Column(nullable = false)
    var productName: String,

    @Column(nullable = false)
    var quantity: Int,

    @Column(nullable = false, precision = 10, scale = 2)
    var totalPrice: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: OrderStatus = OrderStatus.PENDING,

    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)