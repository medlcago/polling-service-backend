package com.backend.pollingservice.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.util.*

data class UserRegisterRequest(
    @field:NotBlank(message = "Username cannot be empty")
    @field:Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @field:Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username can only contain alphanumeric characters")
    val username: String,

    @field:NotBlank(message = "Password cannot be empty")
    @field:Size(min = 6, message = "Minimum password length 6 characters")
    val password: String
)

data class UserResponseDTO(
    val id: UUID,
    val username: String
)
