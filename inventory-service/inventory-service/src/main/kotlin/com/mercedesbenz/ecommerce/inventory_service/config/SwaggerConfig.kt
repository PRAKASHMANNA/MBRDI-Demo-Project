package com.mercedesbenz.ecommerce.inventory_service.config


import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("Inventory Service API")
                .description("Mercedes-Benz E-Commerce - Inventory Service")
                .version("1.0.0")
        )
}