package com.backend.pollingservice.dto

import com.backend.pollingservice.entities.Poll
import com.backend.pollingservice.enums.PollStatus
import com.backend.pollingservice.enums.PollType
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Instant
import java.util.*

data class PollOptionRequest(
    @field:NotBlank(message = "Option text cannot be blank")
    val text: String,


    @field:JsonProperty("is_correct")
    val isCorrect: Boolean? = null,
)

data class CreatePollRequest(
    @field:NotBlank(message = "Question text cannot be blank")
    val question: String,

    val type: PollType,

    val anonymous: Boolean = false,

    @field:Valid
    @field:Size(min = 2, max = 10, message = "Poll must have between 2 and 10 options")
    val options: List<PollOptionRequest>,
)


data class PollOptionResponse(
    val id: UUID,
    val text: String,

    @get:JsonProperty("is_correct")
    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    val isCorrect: Boolean? = null,

    @get:JsonProperty("vote_count")
    val voteCount: Long
)

data class PollResponse(
    val id: UUID,
    val question: String,
    val type: PollType,
    val anonymous: Boolean,
    val status: PollStatus,

    @get:JsonProperty("created_by")
    val createdBy: UUID,

    @get:JsonProperty("created_at")
    val createdAt: Instant,

    @get:JsonProperty("updated_at")
    val updatedAt: Instant,

    val options: List<PollOptionResponse>,

    @get:JsonProperty("user_selected_options")
    val userSelectedOptions: List<UUID>,
) {
    companion object {
        fun fromPoll(poll: Poll, selectedOptionIds: List<UUID>): PollResponse {
            return PollResponse(
                id = poll.id!!,
                question = poll.question,
                type = poll.type,
                anonymous = poll.anonymous,
                status = poll.status,
                createdBy = poll.createdBy.id!!,
                createdAt = poll.createdAt!!,
                updatedAt = poll.updatedAt!!,
                options = poll.options.map {
                    PollOptionResponse(
                        id = it.id!!,
                        text = it.text,
                        isCorrect = it.isCorrect,
                        voteCount = it.voteCount,
                    )
                },
                userSelectedOptions = selectedOptionIds,
            )
        }
    }
}

data class VotePollRequest(
    @field:Size(min = 1, max = 10)
    val options: List<UUID>,
)