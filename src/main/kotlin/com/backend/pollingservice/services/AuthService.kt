package com.backend.pollingservice.services

import com.backend.pollingservice.dto.UserRegisterRequest
import com.backend.pollingservice.dto.UserResponseDTO
import com.backend.pollingservice.entities.User
import com.backend.pollingservice.exceptions.BadRequestException
import com.backend.pollingservice.mappers.toResponse
import com.backend.pollingservice.repositories.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun register(request: UserRegisterRequest): UserResponseDTO {
        val exists = userRepository.existsByUsername(request.username)
        if (exists) throw BadRequestException("User `${request.username}` already exists")

        val user = User(
            username = request.username,
            password = passwordEncoder.encode(request.password)
        )

        val result = userRepository.save(user)
        return result.toResponse()
    }
}