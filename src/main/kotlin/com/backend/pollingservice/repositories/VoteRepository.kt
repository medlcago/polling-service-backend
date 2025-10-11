package com.backend.pollingservice.repositories

import com.backend.pollingservice.entities.Poll
import com.backend.pollingservice.entities.User
import com.backend.pollingservice.entities.Vote
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface VoteRepository : JpaRepository<Vote, UUID> {
    fun findAllByPollAndUser(poll: Poll, user: User): List<Vote>

    fun deleteAllByPollIdAndUserId(pollId: UUID, userId: UUID): Int

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
}