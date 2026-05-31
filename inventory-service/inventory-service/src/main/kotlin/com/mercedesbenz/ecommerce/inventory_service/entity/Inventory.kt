package com.mercedesbenz.ecommerce.inventory_service.entity


import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "inventory")
class Inventory(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false, unique = true)
    var productId: Long,

    @Column(nullable = false)
    var productName: String,

    @Column(nullable = false)
    var availableStock: Int = 0,

    @Column(nullable = false)
    var reservedStock: Int = 0,

    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)