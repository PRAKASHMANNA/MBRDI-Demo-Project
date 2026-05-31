package com.mercedesbenz.ecommerce.user_service.service



import com.mercedesbenz.ecommerce.user_service.entity.UserDto
import com.mercedesbenz.ecommerce.user_service.entity.UserRequestDto

interface UserService {
    fun createUser(request: UserRequestDto): UserDto
    fun getUserById(id: Long): UserDto
    fun getUserByEmail(email: String): UserDto
    fun getAllUsers(): List<UserDto>
    fun updateUser(id: Long, request: UserRequestDto): UserDto
    fun deleteUser(id: Long)
}