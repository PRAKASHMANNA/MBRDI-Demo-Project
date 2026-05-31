package com.mercedesbenz.ecommerce.product_service.service

import com.mercedesbenz.ecommerce.product_service.dto.ProductRequestDto
import com.mercedesbenz.ecommerce.product_service.entity.Product
import com.mercedesbenz.ecommerce.product_service.exception.BadRequestException
import com.mercedesbenz.ecommerce.product_service.exception.ResourceNotFoundException
import com.mercedesbenz.ecommerce.product_service.repository.ProductRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.util.Optional

class ProductServiceTest {

    private val productRepository: ProductRepository = mock()
    private val productService: ProductService = ProductServiceImpl(productRepository)

    private fun mockProduct(): Product {
        return Product(
            id = 1L,
            name = "Mercedes C-Class",
            description = "Luxury sedan",
            price = BigDecimal("45000.00"),
            category = "Sedan",
            stock = 10
        )
    }

    private fun mockRequest(): ProductRequestDto {
        return ProductRequestDto(
            name = "Mercedes C-Class",
            description = "Luxury sedan",
            price = BigDecimal("45000.00"),
            category = "Sedan",
            stock = 10
        )
    }

    @Test
    fun `createProduct - success`() {
        val request = mockRequest()
        val product = mockProduct()

        whenever(productRepository.existsByName(request.name)).thenReturn(false)

        doReturn(product)
            .whenever(productRepository)
            .save(any<Product>())

        val result = productService.createProduct(request)

        assertEquals(product.name, result.name)
        assertEquals(product.price, result.price)
        assertEquals(product.category, result.category)
        assertEquals(product.stock, result.stock)

        verify(productRepository, times(1)).existsByName(request.name)
        verify(productRepository, times(1)).save(any<Product>())
    }

    @Test
    fun `createProduct - throws BadRequestException when name exists`() {
        val request = mockRequest()

        whenever(productRepository.existsByName(request.name)).thenReturn(true)

        assertThrows<BadRequestException> {
            productService.createProduct(request)
        }

        verify(productRepository, times(1)).existsByName(request.name)
        verify(productRepository, never()).save(any<Product>())
    }

    @Test
    fun `getProductById - success`() {
        val product = mockProduct()

        whenever(productRepository.findById(1L)).thenReturn(Optional.of(product))

        val result = productService.getProductById(1L)

        assertEquals(1L, result.id)
        assertEquals(product.name, result.name)
        assertEquals(product.price, result.price)

        verify(productRepository, times(1)).findById(1L)
    }

    @Test
    fun `getProductById - throws ResourceNotFoundException`() {
        whenever(productRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<ResourceNotFoundException> {
            productService.getProductById(99L)
        }

        verify(productRepository, times(1)).findById(99L)
    }

    @Test
    fun `getAllProducts - returns list`() {
        val product = mockProduct()

        whenever(productRepository.findAll()).thenReturn(listOf(product))

        val result = productService.getAllProducts()

        assertEquals(1, result.size)
        assertEquals(product.name, result[0].name)
        assertEquals(product.price, result[0].price)

        verify(productRepository, times(1)).findAll()
    }

    @Test
    fun `updateStock - success`() {
        val product = mockProduct()

        whenever(productRepository.findById(1L)).thenReturn(Optional.of(product))

        doReturn(product)
            .whenever(productRepository)
            .save(any<Product>())

        val result = productService.updateStock(1L, -3)

        assertEquals(product.name, result.name)
        assertEquals(7, result.stock)

        verify(productRepository, times(1)).findById(1L)
        verify(productRepository, times(1)).save(any<Product>())
    }

    @Test
    fun `updateStock - throws BadRequestException when insufficient stock`() {
        val product = mockProduct()

        whenever(productRepository.findById(1L)).thenReturn(Optional.of(product))

        assertThrows<BadRequestException> {
            productService.updateStock(1L, -100)
        }

        verify(productRepository, times(1)).findById(1L)
        verify(productRepository, never()).save(any<Product>())
    }

    @Test
    fun `deleteProduct - success`() {
        whenever(productRepository.existsById(1L)).thenReturn(true)

        doNothing()
            .whenever(productRepository)
            .deleteById(1L)

        productService.deleteProduct(1L)

        verify(productRepository, times(1)).existsById(1L)
        verify(productRepository, times(1)).deleteById(1L)
    }

    @Test
    fun `deleteProduct - throws ResourceNotFoundException`() {
        whenever(productRepository.existsById(99L)).thenReturn(false)

        assertThrows<ResourceNotFoundException> {
            productService.deleteProduct(99L)
        }

        verify(productRepository, times(1)).existsById(99L)
        verify(productRepository, never()).deleteById(99L)
    }
}