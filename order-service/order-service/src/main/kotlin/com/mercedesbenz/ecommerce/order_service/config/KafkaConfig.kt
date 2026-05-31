package com.mercedesbenz.ecommerce.order_service.config


import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class KafkaConfig {

    @Bean
    fun orderPlacedTopic(): NewTopic =
        TopicBuilder.name("order-placed")
            .partitions(1)
            .replicas(1)
            .build()
}