package com.mercedesbenz.ecommerce.inventory_service.repository


import com.mercedesbenz.ecommerce.inventory_service.entity.Inventory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface InventoryRepository : JpaRepository<Inventory, Long> {
    fun findByProductId(productId: Long): Optional<Inventory>
    fun existsByProductId(productId: Long): Boolean
}