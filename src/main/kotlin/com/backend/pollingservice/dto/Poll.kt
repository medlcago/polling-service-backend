package com.backend.pollingservice.dto

import com.backend.pollingservice.enums.PollStatus
import com.backend.pollingservice.enums.PollType
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonUnwrapped
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


data class VotePollRequest(
    @field:Size(min = 1, max = 10)
    val options: List<UUID>,
)


sealed class PollOptionResponseDTO {
    data class Base(
        val id: UUID,
        val text: String,

        @get:JsonProperty("vote_count")
        val voteCount: Long,

        val percent: Int,
    )

    data class PublicResponse(
        @get:JsonUnwrapped
        val base: Base,
    ) : PollOptionResponseDTO()

    data class DetailedResponse(
        @get:JsonUnwrapped
        val base: Base,

        @get:JsonInclude(JsonInclude.Include.NON_NULL)
        @get:JsonProperty("is_correct")
        val isCorrect: Boolean?,
    ) : PollOptionResponseDTO()
}

sealed class PollResponseDTO {
    data class Base(
        val id: UUID,
        val question: String,
        val type: PollType,
        val anonymous: Boolean,
        val status: PollStatus,

        @get:JsonProperty("created_by")
        val createdBy: UUID,

        @get:JsonProperty("created_at")
        val createdAt: Instant,

        @get:JsonProperty("total_votes")
        val totalVotes: Int,

        @get:JsonProperty("user_selected_options")
        val userSelectedOptions: List<UUID>,

        val options: List<PollOptionResponseDTO>
    )

    data class PublicResponse(
        @get:JsonUnwrapped
        val base: Base
    ) : PollResponseDTO()

    data class DetailedResponse(
        @get:JsonUnwrapped
        val base: Base,
        @get:JsonProperty("updated_at")
        val updatedAt: Instant

    ) : PollResponseDTO()
}

data class PollMemberResponseDTO(
    @get:JsonProperty("user_id")
    val userId: UUID,

    val username: String,

    @get:JsonProperty("selected_options")
    val selectedOptions: String
)

data class VotePollResponseDTO(
    val id: UUID,

    @get:JsonProperty("is_quiz")
    val isQuiz: Boolean,

    @get:JsonProperty("is_correct")
    val isCorrect: Boolean?,
)