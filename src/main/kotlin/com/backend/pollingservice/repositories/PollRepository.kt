package com.backend.pollingservice.repositories

import com.backend.pollingservice.entities.Poll
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
}