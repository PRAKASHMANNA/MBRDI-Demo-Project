package com.mercedesbenz.ecommerce.common_lib.dto


import java.math.BigDecimal
import java.time.LocalDateTime

data class ProductDto(
    val id: Long? = null,
    val name: String,
    val description: String? = null,
    val price: BigDecimal,
    val category: String,
    val stock: Int = 0,
    val createdAt: LocalDateTime? = null
)

data class ProductRequestDto(
    val name: String,
    val description: String? = null,
    val price: BigDecimal,
    val category: String,
    val stock: Int
)