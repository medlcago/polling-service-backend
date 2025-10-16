package com.backend.pollingservice.repositories

import com.backend.pollingservice.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    @Query("SELECT u FROM User u WHERE LOWER(u.username) = LOWER(:username)")
    fun findByUsername(username: String): User?


    @Query("SELECT (COUNT(u) > 0) FROM User u WHERE LOWER(u.username) = LOWER(:username)")
    fun existsByUsername(username: String): Boolean
}