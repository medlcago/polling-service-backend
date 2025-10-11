package com.backend.pollingservice.repositories

import com.backend.pollingservice.entities.Poll
import com.backend.pollingservice.entities.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PollRepository : JpaRepository<Poll, UUID> {
    @EntityGraph(attributePaths = ["options"])
    override fun findById(id: UUID): Optional<Poll>

    @EntityGraph(attributePaths = ["options"])
    override fun findAll(): List<Poll>

    @EntityGraph(attributePaths = ["options"])
    override fun findAll(pageable: Pageable): Page<Poll>

    fun findByIdAndCreatedBy(id: UUID, createdBy: User): Poll?
}