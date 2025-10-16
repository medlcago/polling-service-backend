package com.backend.pollingservice.controllers.v1

import com.backend.pollingservice.dto.ApiResponse
import com.backend.pollingservice.dto.UserRegisterRequest
import com.backend.pollingservice.dto.UserResponseDTO
import com.backend.pollingservice.mappers.toResponse
import com.backend.pollingservice.security.UserDetails
import com.backend.pollingservice.services.AuthService
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthControllerV1(
    private val authService: AuthService,
) {
    @PostMapping("/register")
    fun register(@Valid @RequestBody request: UserRegisterRequest): ApiResponse<UserResponseDTO> {
        val user = authService.register(request)
        return ApiResponse.success(user)
    }

    @GetMapping("/user")
    fun getUser(@AuthenticationPrincipal user: UserDetails): ApiResponse<UserResponseDTO> {
        val user = user.getUser()
        return ApiResponse.success(user.toResponse())
    }
}