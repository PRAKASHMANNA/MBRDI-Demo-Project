package com.mercedesbenz.ecommerce.product_service.service


import com.mercedesbenz.ecommerce.product_service.dto.ProductDto
import com.mercedesbenz.ecommerce.product_service.dto.ProductRequestDto
import com.mercedesbenz.ecommerce.product_service.entity.Product
import com.mercedesbenz.ecommerce.product_service.exception.BadRequestException

import com.mercedesbenz.ecommerce.product_service.exception.ResourceNotFoundException
import com.mercedesbenz.ecommerce.product_service.repository.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository
) : ProductService {

    private val logger = LoggerFactory.getLogger(ProductServiceImpl::class.java)

    override fun createProduct(request: ProductRequestDto): ProductDto {
        logger.info("Creating product: {}", request.name)

        if (productRepository.existsByName(request.name)) {
            logger.warn("Product already exists: {}", request.name)
            throw BadRequestException("Product already exists: ${request.name}")
        }

        val product = Product(
            name = request.name,
            description = request.description,
            price = request.price,
            category = request.category,
            stock = request.stock
        )

        val saved = productRepository.save(product)
        logger.info("Product created with id: {}", saved.id)
        return saved.toDto()
    }

    override fun getProductById(id: Long): ProductDto {
        logger.info("Fetching product by id: {}", id)
        val product = productRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Product not found with id: $id") }
        return product.toDto()
    }

    override fun getAllProducts(): List<ProductDto> {
        logger.info("Fetching all products")
        return productRepository.findAll().map { it.toDto() }
    }

    override fun getProductsByCategory(category: String): List<ProductDto> {
        logger.info("Fetching products by category: {}", category)
        return productRepository.findByCategory(category).map { it.toDto() }
    }

    override fun searchProducts(name: String): List<ProductDto> {
        logger.info("Searching products by name: {}", name)
        return productRepository.findByNameContainingIgnoreCase(name).map { it.toDto() }
    }

    override fun updateProduct(id: Long, request: ProductRequestDto): ProductDto {
        logger.info("Updating product with id: {}", id)
        val existing = productRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Product not found with id: $id") }

        existing.name = request.name
        existing.description = request.description
        existing.price = request.price
        existing.category = request.category
        existing.stock = request.stock
        existing.updatedAt = LocalDateTime.now()

        val saved = productRepository.save(existing)
        logger.info("Product updated with id: {}", saved.id)
        return saved.toDto()
    }

    override fun deleteProduct(id: Long) {
        logger.info("Deleting product with id: {}", id)
        if (!productRepository.existsById(id)) {
            throw ResourceNotFoundException("Product not found with id: $id")
        }
        productRepository.deleteById(id)
        logger.info("Product deleted with id: {}", id)
    }

    override fun updateStock(id: Long, quantity: Int): ProductDto {
        logger.info("Updating stock for product id: {}, quantity: {}", id, quantity)
        val product = productRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Product not found with id: $id") }

        if (product.stock + quantity < 0) {
            throw BadRequestException("Insufficient stock for product id: $id")
        }

        product.stock += quantity
        product.updatedAt = LocalDateTime.now()

        val saved = productRepository.save(product)
        logger.info("Stock updated for product id: {}, new stock: {}", id, saved.stock)
        return saved.toDto()
    }

    private fun Product.toDto() = ProductDto(
        id = this.id,
        name = this.name,
        description = this.description,
        price = this.price,
        category = this.category,
        stock = this.stock,
        createdAt = this.createdAt
    )
}