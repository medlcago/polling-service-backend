package com.backend.pollingservice.mappers

import com.backend.pollingservice.dto.UserResponseDTO
import com.backend.pollingservice.entities.User

fun User.toResponse(): UserResponseDTO {
    return UserResponseDTO(
        id = id!!,
        username = username,
    )
}