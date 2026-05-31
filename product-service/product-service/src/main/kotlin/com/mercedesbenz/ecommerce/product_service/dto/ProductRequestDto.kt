package com.mercedesbenz.ecommerce.product_service.dto


import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class ProductRequestDto(

    @field:NotBlank(message = "Product name is required")
    val name: String,

    val description: String? = null,

    @field:NotNull(message = "Price is required")
    @field:DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    val price: BigDecimal,

    @field:NotBlank(message = "Category is required")
    val category: String,

    @field:Min(value = 0, message = "Stock cannot be negative")
    val stock: Int
)