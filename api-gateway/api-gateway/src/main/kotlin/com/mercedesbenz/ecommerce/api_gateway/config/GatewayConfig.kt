package com.mercedesbenz.ecommerce.api_gateway.config


import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GatewayConfig {

    private val logger = LoggerFactory.getLogger(GatewayConfig::class.java)

    @Bean
    fun customRouteLocator(builder: RouteLocatorBuilder): RouteLocator {
        logger.info("Configuring API Gateway routes")

        return builder.routes()
            .route("user-service") { r ->
                r.path("/api/v1/users/**")
                    .uri("http://localhost:8081")
            }
            .route("product-service") { r ->
                r.path("/api/v1/products/**")
                    .uri("http://localhost:8082")
            }
            .route("order-service") { r ->
                r.path("/api/v1/orders/**")
                    .uri("http://localhost:8083")
            }
            .route("inventory-service") { r ->
                r.path("/api/v1/inventory/**")
                    .uri("http://localhost:8084")
            }
            .route("notification-service") { r ->
                r.path("/api/v1/notifications/**")
                    .uri("http://localhost:8085")
            }
            .build()
    }
}