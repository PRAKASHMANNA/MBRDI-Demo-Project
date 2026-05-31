package com.mercedesbenz.ecommerce.inventory_service.service

import com.mercedesbenz.ecommerce.inventory_service.dto.InventoryRequestDto
import com.mercedesbenz.ecommerce.inventory_service.entity.Inventory
import com.mercedesbenz.ecommerce.inventory_service.exception.BadRequestException
import com.mercedesbenz.ecommerce.inventory_service.exception.ResourceNotFoundException
import com.mercedesbenz.ecommerce.inventory_service.repository.InventoryRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Optional

class InventoryServiceTest {

    private val inventoryRepository: InventoryRepository = mock()
    private val inventoryService: InventoryService = InventoryServiceImpl(inventoryRepository)

    private fun mockInventory(
        availableStock: Int = 10,
        reservedStock: Int = 0
    ): Inventory {
        return Inventory(
            id = 1L,
            productId = 1L,
            productName = "Mercedes C-Class",
            availableStock = availableStock,
            reservedStock = reservedStock
        )
    }

    private fun mockRequest(): InventoryRequestDto {
        return InventoryRequestDto(
            productId = 1L,
            productName = "Mercedes C-Class",
            availableStock = 10
        )
    }

    @Test
    fun `addInventory - success`() {
        val inventory = mockInventory()
        val request = mockRequest()

        whenever(inventoryRepository.existsByProductId(1L))
            .thenReturn(false)

        doReturn(inventory)
            .whenever(inventoryRepository)
            .save(any<Inventory>())

        val result = inventoryService.addInventory(request)

        assertEquals(inventory.productId, result.productId)
        assertEquals(inventory.availableStock, result.availableStock)

        verify(inventoryRepository, times(1)).existsByProductId(1L)
        verify(inventoryRepository, times(1)).save(any<Inventory>())
    }

    @Test
    fun `addInventory - throws BadRequestException when already exists`() {
        val request = mockRequest()

        whenever(inventoryRepository.existsByProductId(1L))
            .thenReturn(true)

        assertThrows<BadRequestException> {
            inventoryService.addInventory(request)
        }

        verify(inventoryRepository, times(1)).existsByProductId(1L)
        verify(inventoryRepository, never()).save(any<Inventory>())
    }

    @Test
    fun `getInventoryByProductId - success`() {
        val inventory = mockInventory()

        whenever(inventoryRepository.findByProductId(1L))
            .thenReturn(Optional.of(inventory))

        val result = inventoryService.getInventoryByProductId(1L)

        assertEquals(1L, result.productId)
        assertEquals(10, result.availableStock)

        verify(inventoryRepository, times(1)).findByProductId(1L)
    }

    @Test
    fun `getInventoryByProductId - throws ResourceNotFoundException`() {
        whenever(inventoryRepository.findByProductId(99L))
            .thenReturn(Optional.empty())

        assertThrows<ResourceNotFoundException> {
            inventoryService.getInventoryByProductId(99L)
        }

        verify(inventoryRepository, times(1)).findByProductId(99L)
    }

    @Test
    fun `reduceStock - success`() {
        val inventory = mockInventory()

        whenever(inventoryRepository.findByProductId(1L))
            .thenReturn(Optional.of(inventory))

        doAnswer { invocation ->
            invocation.getArgument<Inventory>(0)
        }.whenever(inventoryRepository).save(any<Inventory>())

        inventoryService.reduceStock(1L, 3)

        verify(inventoryRepository, times(1)).findByProductId(1L)
        verify(inventoryRepository, times(1)).save(any<Inventory>())
    }

    @Test
    fun `reduceStock - throws BadRequestException when insufficient stock`() {
        val inventory = mockInventory()

        whenever(inventoryRepository.findByProductId(1L))
            .thenReturn(Optional.of(inventory))

        assertThrows<BadRequestException> {
            inventoryService.reduceStock(1L, 100)
        }

        verify(inventoryRepository, times(1)).findByProductId(1L)
        verify(inventoryRepository, never()).save(any<Inventory>())
    }

    @Test
    fun `updateStock - success`() {
        val inventory = mockInventory()

        whenever(inventoryRepository.findByProductId(1L))
            .thenReturn(Optional.of(inventory))

        doAnswer { invocation ->
            invocation.getArgument<Inventory>(0)
        }.whenever(inventoryRepository).save(any<Inventory>())

        val result = inventoryService.updateStock(1L, 5)

        assertEquals(15, result.availableStock)

        verify(inventoryRepository, times(1)).findByProductId(1L)
        verify(inventoryRepository, times(1)).save(any<Inventory>())
    }

    @Test
    fun `updateStock - throws BadRequestException when stock goes below zero`() {
        val inventory = mockInventory()

        whenever(inventoryRepository.findByProductId(1L))
            .thenReturn(Optional.of(inventory))

        assertThrows<BadRequestException> {
            inventoryService.updateStock(1L, -100)
        }

        verify(inventoryRepository, times(1)).findByProductId(1L)
        verify(inventoryRepository, never()).save(any<Inventory>())
    }

    @Test
    fun `getAllInventory - returns list`() {
        val inventory = mockInventory()

        whenever(inventoryRepository.findAll())
            .thenReturn(listOf(inventory))

        val result = inventoryService.getAllInventory()

        assertEquals(1, result.size)
        assertEquals(inventory.productId, result[0].productId)

        verify(inventoryRepository, times(1)).findAll()
    }
}