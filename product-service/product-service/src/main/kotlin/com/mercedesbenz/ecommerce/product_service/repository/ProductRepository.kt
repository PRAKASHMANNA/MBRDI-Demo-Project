package com.mercedesbenz.ecommerce.product_service.repository


import com.mercedesbenz.ecommerce.product_service.entity.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    fun findByCategory(category: String): List<Product>
    fun findByNameContainingIgnoreCase(name: String): List<Product>
    fun existsByName(name: String): Boolean
}