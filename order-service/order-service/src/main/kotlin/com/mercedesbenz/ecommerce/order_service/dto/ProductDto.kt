package com.mercedesbenz.ecommerce.order_service.dto


import java.math.BigDecimal

data class ProductDto(
    val id: Long,
    val name: String,
    val price: BigDecimal,
    val stock: Int,
    val category: String
)