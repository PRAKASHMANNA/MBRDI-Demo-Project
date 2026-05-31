package com.mercedesbenz.ecommerce.user_service.service

import com.mercedesbenz.ecommerce.user_service.entity.User
import com.mercedesbenz.ecommerce.user_service.entity.UserRequestDto
import com.mercedesbenz.ecommerce.user_service.exception.BadRequestException
import com.mercedesbenz.ecommerce.user_service.exception.ResourceNotFoundException
import com.mercedesbenz.ecommerce.user_service.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Optional

class UserServiceTest {

    private val userRepository: UserRepository = mock()
    private val userService: UserService = UserServiceImpl(userRepository)

    private fun mockUser(): User {
        return User(
            id = 1L,
            name = "John Doe",
            email = "john@mercedesbenz.com",
            password = "password123",
            phone = "9876543210"
        )
    }

    private fun mockRequest(): UserRequestDto {
        return UserRequestDto(
            name = "John Doe",
            email = "john@mercedesbenz.com",
            password = "password123",
            phone = "9876543210"
        )
    }

    @Test
    fun `createUser - success`() {
        val request = mockRequest()
        val user = mockUser()

        whenever(userRepository.existsByEmail(request.email)).thenReturn(false)

        doReturn(user)
            .whenever(userRepository)
            .save(any<User>())

        val result = userService.createUser(request)

        assertEquals(user.name, result.name)
        assertEquals(user.email, result.email)

        verify(userRepository, times(1)).existsByEmail(request.email)
        verify(userRepository, times(1)).save(any<User>())
    }

    @Test
    fun `createUser - throws BadRequestException when email exists`() {
        val request = mockRequest()

        whenever(userRepository.existsByEmail(request.email)).thenReturn(true)

        assertThrows<BadRequestException> {
            userService.createUser(request)
        }

        verify(userRepository, times(1)).existsByEmail(request.email)
        verify(userRepository, never()).save(any<User>())
    }

    @Test
    fun `getUserById - success`() {
        val user = mockUser()

        whenever(userRepository.findById(1L)).thenReturn(Optional.of(user))

        val result = userService.getUserById(1L)

        assertEquals(1L, result.id)
        assertEquals(user.name, result.name)
        assertEquals(user.email, result.email)

        verify(userRepository, times(1)).findById(1L)
    }

    @Test
    fun `getUserById - throws ResourceNotFoundException`() {
        whenever(userRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<ResourceNotFoundException> {
            userService.getUserById(99L)
        }

        verify(userRepository, times(1)).findById(99L)
    }

    @Test
    fun `updateUser - success`() {
        val request = mockRequest()
        val existingUser = mockUser()

        whenever(userRepository.findById(1L)).thenReturn(Optional.of(existingUser))

        doReturn(existingUser)
            .whenever(userRepository)
            .save(any<User>())

        val result = userService.updateUser(1L, request)

        assertEquals(existingUser.name, result.name)
        assertEquals(existingUser.email, result.email)

        verify(userRepository, times(1)).findById(1L)
        verify(userRepository, times(1)).save(any<User>())
    }

    @Test
    fun `updateUser - throws ResourceNotFoundException`() {
        val request = mockRequest()

        whenever(userRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<ResourceNotFoundException> {
            userService.updateUser(99L, request)
        }

        verify(userRepository, times(1)).findById(99L)
        verify(userRepository, never()).save(any<User>())
    }

    @Test
    fun `deleteUser - success`() {
        whenever(userRepository.existsById(1L)).thenReturn(true)

        doNothing()
            .whenever(userRepository)
            .deleteById(1L)

        userService.deleteUser(1L)

        verify(userRepository, times(1)).existsById(1L)
        verify(userRepository, times(1)).deleteById(1L)
    }

    @Test
    fun `deleteUser - throws ResourceNotFoundException`() {
        whenever(userRepository.existsById(99L)).thenReturn(false)

        assertThrows<ResourceNotFoundException> {
            userService.deleteUser(99L)
        }

        verify(userRepository, times(1)).existsById(99L)
        verify(userRepository, never()).deleteById(99L)
    }

    @Test
    fun `getAllUsers - returns list`() {
        val user = mockUser()

        whenever(userRepository.findAll()).thenReturn(listOf(user))

        val result = userService.getAllUsers()

        assertEquals(1, result.size)
        assertEquals(user.name, result[0].name)
        assertEquals(user.email, result[0].email)

        verify(userRepository, times(1)).findAll()
    }
}