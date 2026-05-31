package com.mercedesbenz.ecommerce.order_service.client


import com.mercedesbenz.ecommerce.order_service.dto.ApiResponse
import com.mercedesbenz.ecommerce.order_service.dto.ProductDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(name = "product-service", url = "\${product.service.url}")
interface ProductClient {

    @GetMapping("/api/v1/products/{id}")
    fun getProductById(@PathVariable id: Long): ApiResponse<ProductDto>
}