package com.mercedesbenz.ecommerce.user_service.controller


import com.mercedesbenz.ecommerce.user_service.entity.ApiResponse
import com.mercedesbenz.ecommerce.user_service.entity.UserDto
import com.mercedesbenz.ecommerce.user_service.entity.UserRequestDto
import com.mercedesbenz.ecommerce.user_service.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Service", description = "APIs for managing users")
class UserController(
    private val userService: UserService
) {

    private val logger = LoggerFactory.getLogger(UserController::class.java)

    @PostMapping
    @Operation(summary = "Create a new user")
    fun createUser(@Valid @RequestBody request: UserRequestDto): ResponseEntity<ApiResponse<UserDto>> {
        logger.info("POST /api/v1/users - creating user")
        val user = userService.createUser(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(user, "User created successfully"))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    fun getUserById(@PathVariable id: Long): ResponseEntity<ApiResponse<UserDto>> {
        logger.info("GET /api/v1/users/{}", id)
        val user = userService.getUserById(id)
        return ResponseEntity.ok(ApiResponse.success(user))
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email")
    fun getUserByEmail(@PathVariable email: String): ResponseEntity<ApiResponse<UserDto>> {
        logger.info("GET /api/v1/users/email/{}", email)
        val user = userService.getUserByEmail(email)
        return ResponseEntity.ok(ApiResponse.success(user))
    }

    @GetMapping
    @Operation(summary = "Get all users")
    fun getAllUsers(): ResponseEntity<ApiResponse<List<UserDto>>> {
        logger.info("GET /api/v1/users - fetching all users")
        val users = userService.getAllUsers()
        return ResponseEntity.ok(ApiResponse.success(users))
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user by ID")
    fun updateUser(
        @PathVariable id: Long,
        @Valid @RequestBody request: UserRequestDto
    ): ResponseEntity<ApiResponse<UserDto>> {
        logger.info("PUT /api/v1/users/{}", id)
        val user = userService.updateUser(id, request)
        return ResponseEntity.ok(ApiResponse.success(user, "User updated successfully"))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user by ID")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<ApiResponse<String>> {
        logger.info("DELETE /api/v1/users/{}", id)
        userService.deleteUser(id)
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully"))
    }
}