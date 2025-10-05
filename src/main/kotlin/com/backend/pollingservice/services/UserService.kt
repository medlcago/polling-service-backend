package com.backend.pollingservice.services

import com.backend.pollingservice.repositories.UserRepository
import com.backend.pollingservice.user.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) : UserDetailsService {
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): org.springframework.security.core.userdetails.UserDetails {
        val user = userRepository.findByUsername(username) ?: throw UsernameNotFoundException("User not found")
        return UserDetails(user)
    }
}