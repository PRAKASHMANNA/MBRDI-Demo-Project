package com.mercedesbenz.ecommerce.user_service.service


import com.mercedesbenz.ecommerce.user_service.entity.UserDto
import com.mercedesbenz.ecommerce.user_service.entity.UserRequestDto
import com.mercedesbenz.ecommerce.user_service.exception.BadRequestException
import com.mercedesbenz.ecommerce.user_service.exception.ResourceNotFoundException
import com.mercedesbenz.ecommerce.user_service.entity.User
import com.mercedesbenz.ecommerce.user_service.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {

    private val logger = LoggerFactory.getLogger(UserServiceImpl::class.java)

    override fun createUser(request: UserRequestDto): UserDto {
        logger.info("Creating user with email: {}", request.email)

        if (userRepository.existsByEmail(request.email)) {
            logger.warn("Email already exists: {}", request.email)
            throw BadRequestException("Email already registered: ${request.email}")
        }

        val user = User(
            name = request.name,
            email = request.email,
            password = request.password,
            phone = request.phone
        )

        val saved = userRepository.save(user)
        logger.info("User created successfully with id: {}", saved.id)
        return saved.toDto()
    }

    override fun getUserById(id: Long): UserDto {
        logger.info("Fetching user by id: {}", id)
        val user = userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User not found with id: $id") }
        return user.toDto()
    }

    override fun getUserByEmail(email: String): UserDto {
        logger.info("Fetching user by email: {}", email)
        val user = userRepository.findByEmail(email)
            .orElseThrow { ResourceNotFoundException("User not found with email: $email") }
        return user.toDto()
    }

    override fun getAllUsers(): List<UserDto> {
        logger.info("Fetching all users")
        return userRepository.findAll().map { it.toDto() }
    }

    override fun updateUser(id: Long, request: UserRequestDto): UserDto {
        logger.info("Updating user with id: {}", id)
        val existing = userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User not found with id: $id") }

        val updated = existing.copy(
            name = request.name,
            email = request.email,
            password = request.password,
            phone = request.phone,
            updatedAt = LocalDateTime.now()
        )

        val saved = userRepository.save(updated)
        logger.info("User updated successfully with id: {}", saved.id)
        return saved.toDto()
    }

    override fun deleteUser(id: Long) {
        logger.info("Deleting user with id: {}", id)
        if (!userRepository.existsById(id)) {
            throw ResourceNotFoundException("User not found with id: $id")
        }
        userRepository.deleteById(id)
        logger.info("User deleted successfully with id: {}", id)
    }

    private fun User.toDto() = UserDto(
        id = this.id,
        name = this.name,
        email = this.email,
        phone = this.phone,
        createdAt = this.createdAt
    )
}