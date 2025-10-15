package com.backend.pollingservice.repositories

import com.backend.pollingservice.entities.Vote
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface VoteRepository : JpaRepository<Vote, UUID> {
    @Query(
        """
    SELECT v.*
    FROM votes v 
    JOIN poll_options po ON v.option_id = po.id 
    WHERE po.poll_id = :pollId AND v.user_id = :userId
""", nativeQuery = true
    )
    fun findAllByPollIdAndUserId(
        @Param("pollId") pollId: UUID,
        @Param("userId") userId: UUID
    ): List<Vote>

    @Modifying
    @Query(
        """
    INSERT INTO votes (user_id, option_id) 
    SELECT :userId, t.option_id
    FROM unnest(:optionIds) AS t(option_id)
""", nativeQuery = true
    )
    fun bulkInsertVotes(
        @Param("userId") userId: UUID,
        @Param("optionIds") optionIds: Array<UUID>
    )

    @Query(
        """
    SELECT 
        v.id as id,
        po.poll_id as pollId,
        v.option_id as optionId
    FROM votes v 
    JOIN poll_options po ON po.id = v.option_id
    WHERE po.poll_id IN (:pollIds) AND v.user_id = :userId
""", nativeQuery = true
    )
    fun findViewByPollIdsAndUserId(pollIds: List<UUID>, userId: UUID): List<View>

    data class View(
        val id: UUID,
        val pollId: UUID,
        val optionId: UUID,
    )
}