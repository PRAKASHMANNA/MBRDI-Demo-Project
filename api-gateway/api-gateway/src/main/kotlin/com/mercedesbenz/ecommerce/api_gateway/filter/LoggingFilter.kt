package com.mercedesbenz.ecommerce.api_gateway.filter


import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class LoggingFilter : GlobalFilter, Ordered {

    private val logger = LoggerFactory.getLogger(LoggingFilter::class.java)

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val request: ServerHttpRequest = exchange.request

        logger.info(
            "Incoming Request → Method: {}, Path: {}, RemoteAddress: {}",
            request.method,
            request.uri.path,
            request.remoteAddress
        )

        return chain.filter(exchange).then(
            Mono.fromRunnable {
                val response = exchange.response
                logger.info(
                    "Outgoing Response → Path: {}, StatusCode: {}",
                    request.uri.path,
                    response.statusCode
                )
            }
        )
    }

    override fun getOrder(): Int = -1
}