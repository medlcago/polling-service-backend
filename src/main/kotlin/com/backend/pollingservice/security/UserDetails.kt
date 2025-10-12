package com.backend.pollingservice.security

import com.backend.pollingservice.entities.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserDetails(
    private val user: User,
) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_USER"))
    }

    override fun getPassword() = user.password

    override fun getUsername() = user.username

    fun getId() = user.id!!

    fun getUser() = user
}