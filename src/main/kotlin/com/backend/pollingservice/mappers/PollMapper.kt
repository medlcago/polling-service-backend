package com.backend.pollingservice.mappers

import com.backend.pollingservice.dto.PollOptionResponseDTO
import com.backend.pollingservice.dto.PollResponseDTO
import com.backend.pollingservice.entities.Poll
import com.backend.pollingservice.entities.PollOption
import java.util.*


fun Poll.toPublicResponse(selectedOptionIds: List<UUID>): PollResponseDTO.PublicResponse {
    return PollResponseDTO.PublicResponse(
        PollResponseDTO.Base(
            id = id!!,
            question = question,
            type = type,
            anonymous = anonymous,
            status = status,
            createdBy = createdBy.id!!,
            createdAt = createdAt!!,
            totalVotes = totalVotes,
            userSelectedOptions = selectedOptionIds,
            options = options.map { it.toPublicResponse() }
        )
    )
}

fun Poll.toDetailedResponse(selectedOptionIds: List<UUID>): PollResponseDTO.DetailedResponse {
    return PollResponseDTO.DetailedResponse(
        PollResponseDTO.Base(
            id = id!!,
            question = question,
            type = type,
            anonymous = anonymous,
            status = status,
            createdBy = createdBy.id!!,
            createdAt = createdAt!!,
            totalVotes = totalVotes,
            userSelectedOptions = selectedOptionIds,
            options = options.map { it.toDetailedResponse() }
        ),
        updatedAt = updatedAt!!,
    )
}

fun PollOption.toPublicResponse() = PollOptionResponseDTO.PublicResponse(
    PollOptionResponseDTO.Base(
        id = id!!,
        text = text,
        voteCount = voteCount,
        percent = percent,
    )
)

fun PollOption.toDetailedResponse() = PollOptionResponseDTO.DetailedResponse(
    PollOptionResponseDTO.Base(
        id = id!!,
        text = text,
        voteCount = voteCount,
        percent = percent,
    ),
    isCorrect = isCorrect,
)