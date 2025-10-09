package com.backend.pollingservice.services

import com.backend.pollingservice.dto.CreatePollRequest
import com.backend.pollingservice.dto.PollResponse
import com.backend.pollingservice.entities.Poll
import com.backend.pollingservice.entities.User
import com.backend.pollingservice.enums.PollStatus
import com.backend.pollingservice.enums.PollType
import com.backend.pollingservice.exceptions.BadRequestException
import com.backend.pollingservice.exceptions.NotFoundException
import com.backend.pollingservice.repositories.PollRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class PollService(
    private val pollRepository: PollRepository
) {
    @Throws(BadRequestException::class)
    fun createPoll(user: User, request: CreatePollRequest): PollResponse {
        validatePoll(request)

        val poll = Poll(
            question = request.question,
            type = request.type,
            anonymous = request.anonymous,
            status = PollStatus.OPEN,
            createdBy = user,
        ).apply {
            request.options.forEach {
                addOption(it.text, it.isCorrect)
            }
        }

        val result = pollRepository.save(poll)
        return PollResponse.fromPoll(result)
    }

    fun getPolls(): List<PollResponse> {
        val polls = pollRepository.findAll()
        return PollResponse.fromPolls(polls)
    }

    @Throws(NotFoundException::class)
    fun getPoll(id: UUID): PollResponse {
        val poll = pollRepository.findById(id).orElseThrow { NotFoundException("Poll not found") }
        return PollResponse.fromPoll(poll)
    }

    @Throws(BadRequestException::class)
    private fun validatePoll(request: CreatePollRequest) {
        when (request.type) {
            PollType.QUIZ -> {
                val correctCount = request.options.count { it.isCorrect == true }

                if (correctCount != 1) {
                    throw BadRequestException("Quiz poll must have exactly one correct option")
                }

                val hasNullCorrect = request.options.any { it.isCorrect == null }
                if (hasNullCorrect) {
                    throw BadRequestException("All options in a quiz poll must specify isCorrect")
                }
            }

            PollType.SINGLE_CHOICE, PollType.MULTIPLE_CHOICE -> {
                val hasCorrectFlag = request.options.any { it.isCorrect != null }
                if (hasCorrectFlag) {
                    throw BadRequestException("Non-quiz polls must not specify isCorrect")
                }
            }
        }
    }
}