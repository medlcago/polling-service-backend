package com.backend.pollingservice.repositories

import com.backend.pollingservice.entities.Vote
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

data class VoteView(
    val id: UUID,
    val pollId: UUID,
    val optionId: UUID,
)

@Repository
interface VoteRepository : JpaRepository<Vote, UUID> {
    @Query("SELECT * FROM votes WHERE poll_id = :pollId AND user_id = :userId", nativeQuery = true)
    fun findAllByPollIdAndUserId(
        @Param("pollId") pollId: UUID,
        @Param("userId") userId: UUID
    ): List<Vote>

    @Modifying
    @Query(
        """
    INSERT INTO votes (user_id, option_id, poll_id) 
    SELECT :userId, t.option_id, :pollId
    FROM unnest(:optionIds) AS t(option_id)
""", nativeQuery = true
    )
    fun bulkInsertVotes(
        @Param("userId") userId: UUID,
        @Param("pollId") pollId: UUID,
        @Param("optionIds") optionIds: Array<UUID>
    )

    @Query(
        """
    SELECT 
        v.id as id,
        v.poll_id as pollId,
        v.option_id as optionId
    FROM votes v 
    WHERE v.poll_id IN (:pollIds) AND v.user_id = :userId
""", nativeQuery = true
    )
    fun findViewByPollIdsAndUserId(pollIds: List<UUID>, userId: UUID): List<VoteView>
}