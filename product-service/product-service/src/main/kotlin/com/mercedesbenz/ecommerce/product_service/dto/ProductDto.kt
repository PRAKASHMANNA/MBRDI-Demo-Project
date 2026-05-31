package com.mercedesbenz.ecommerce.product_service.dto


import java.math.BigDecimal
import java.time.LocalDateTime

data class ProductDto(
    val id: Long? = null,
    val name: String,
    val description: String? = null,
    val price: BigDecimal,
    val category: String,
    val stock: Int,
    val createdAt: LocalDateTime? = null
)