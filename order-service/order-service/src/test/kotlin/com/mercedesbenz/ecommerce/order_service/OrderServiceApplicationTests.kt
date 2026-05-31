package com.mercedesbenz.ecommerce.order_service

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(properties = [
    "product.service.url=http://localhost:8082"
])
class OrderServiceApplicationTests {

	@Test
	fun contextLoads() {
	}

}
