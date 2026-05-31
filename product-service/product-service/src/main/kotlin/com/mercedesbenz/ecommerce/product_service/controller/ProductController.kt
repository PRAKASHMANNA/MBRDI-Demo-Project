package com.mercedesbenz.ecommerce.product_service.controller


import com.mercedesbenz.ecommerce.product_service.dto.ApiResponse
import com.mercedesbenz.ecommerce.product_service.dto.ProductDto
import com.mercedesbenz.ecommerce.product_service.dto.ProductRequestDto
import com.mercedesbenz.ecommerce.product_service.service.ProductService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Product Service", description = "APIs for managing products")
class ProductController(
    private val productService: ProductService
) {

    private val logger = LoggerFactory.getLogger(ProductController::class.java)

    @PostMapping
    @Operation(summary = "Create a new product")
    fun createProduct(
        @Valid @RequestBody request: ProductRequestDto
    ): ResponseEntity<ApiResponse<ProductDto>> {
        logger.info("POST /api/v1/products")
        val product = productService.createProduct(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(product, "Product created successfully"))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    fun getProductById(@PathVariable id: Long): ResponseEntity<ApiResponse<ProductDto>> {
        logger.info("GET /api/v1/products/{}", id)
        val product = productService.getProductById(id)
        return ResponseEntity.ok(ApiResponse.success(product))
    }

    @GetMapping
    @Operation(summary = "Get all products")
    fun getAllProducts(): ResponseEntity<ApiResponse<List<ProductDto>>> {
        logger.info("GET /api/v1/products")
        val products = productService.getAllProducts()
        return ResponseEntity.ok(ApiResponse.success(products))
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get products by category")
    fun getProductsByCategory(
        @PathVariable category: String
    ): ResponseEntity<ApiResponse<List<ProductDto>>> {
        logger.info("GET /api/v1/products/category/{}", category)
        val products = productService.getProductsByCategory(category)
        return ResponseEntity.ok(ApiResponse.success(products))
    }

    @GetMapping("/search")
    @Operation(summary = "Search products by name")
    fun searchProducts(
        @RequestParam name: String
    ): ResponseEntity<ApiResponse<List<ProductDto>>> {
        logger.info("GET /api/v1/products/search?name={}", name)
        val products = productService.searchProducts(name)
        return ResponseEntity.ok(ApiResponse.success(products))
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product by ID")
    fun updateProduct(
        @PathVariable id: Long,
        @Valid @RequestBody request: ProductRequestDto
    ): ResponseEntity<ApiResponse<ProductDto>> {
        logger.info("PUT /api/v1/products/{}", id)
        val product = productService.updateProduct(id, request)
        return ResponseEntity.ok(ApiResponse.success(product, "Product updated successfully"))
    }

    @PatchMapping("/{id}/stock")
    @Operation(summary = "Update product stock")
    fun updateStock(
        @PathVariable id: Long,
        @RequestParam quantity: Int
    ): ResponseEntity<ApiResponse<ProductDto>> {
        logger.info("PATCH /api/v1/products/{}/stock?quantity={}", id, quantity)
        val product = productService.updateStock(id, quantity)
        return ResponseEntity.ok(ApiResponse.success(product, "Stock updated successfully"))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product by ID")
    fun deleteProduct(@PathVariable id: Long): ResponseEntity<ApiResponse<String>> {
        logger.info("DELETE /api/v1/products/{}", id)
        productService.deleteProduct(id)
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully"))
    }
}