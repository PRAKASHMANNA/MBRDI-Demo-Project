package com.mercedesbenz.ecommerce.product_service.service


import com.mercedesbenz.ecommerce.product_service.dto.ProductDto
import com.mercedesbenz.ecommerce.product_service.dto.ProductRequestDto

interface ProductService {
    fun createProduct(request: ProductRequestDto): ProductDto
    fun getProductById(id: Long): ProductDto
    fun getAllProducts(): List<ProductDto>
    fun getProductsByCategory(category: String): List<ProductDto>
    fun searchProducts(name: String): List<ProductDto>
    fun updateProduct(id: Long, request: ProductRequestDto): ProductDto
    fun deleteProduct(id: Long)
    fun updateStock(id: Long, quantity: Int): ProductDto
}