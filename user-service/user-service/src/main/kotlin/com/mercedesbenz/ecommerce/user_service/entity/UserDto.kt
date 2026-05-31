package com.mercedesbenz.ecommerce.user_service.entity


import java.time.LocalDateTime

data class UserDto(
    val id: Long? = null,
    val name: String,
    val email: String,
    val phone: String? = null,
    val createdAt: LocalDateTime? = null
)

data class UserRequestDto(
    val name: String,
    val email: String,
    val password: String,
    val phone: String? = null
)