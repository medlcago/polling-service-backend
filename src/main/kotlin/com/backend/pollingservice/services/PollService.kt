package com.backend.pollingservice.services

import com.backend.pollingservice.dto.CreatePollRequest
import com.backend.pollingservice.dto.PaginatedResponse
import com.backend.pollingservice.dto.PollResponse
import com.backend.pollingservice.dto.VotePollRequest
import com.backend.pollingservice.entities.Poll
import com.backend.pollingservice.entities.User
import com.backend.pollingservice.enums.PollStatus
import com.backend.pollingservice.enums.PollType
import com.backend.pollingservice.exceptions.BadRequestException
import com.backend.pollingservice.exceptions.NotFoundException
import com.backend.pollingservice.repositories.PollRepository
import com.backend.pollingservice.repositories.VoteRepository
import jakarta.persistence.EntityManager
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class PollService(
    private val pollRepository: PollRepository,
    private val voteRepository: VoteRepository,
    private val entityManager: EntityManager,
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

    fun getPolls(limit: Int, offset: Int): PaginatedResponse<PollResponse> {
        val pageNumber = offset / limit
        val pageable = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.ASC, "createdAt"))

        val polls = pollRepository.findAll(pageable)
        val result = PollResponse.fromPolls(polls.content)
        return PaginatedResponse(
            total = polls.totalElements,
            result = result
        )
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

    @Transactional
    @Throws(NotFoundException::class)
    fun closePoll(user: User, pollId: UUID) {
        val poll = pollRepository.findByIdAndCreatedBy(pollId, user) ?: throw NotFoundException("Poll not found")
        if (poll.status == PollStatus.CLOSED) {
            throw BadRequestException("Poll already closed")
        }

        poll.status = PollStatus.CLOSED
        pollRepository.save(poll)
    }

    @Transactional
    @Throws(NotFoundException::class, BadRequestException::class)
    fun votePoll(user: User, pollId: UUID, request: VotePollRequest): PollResponse {
        val poll = pollRepository.findById(pollId).orElseThrow { throw NotFoundException("Poll not found") }
        validateVote(user, poll, request)

        voteRepository.bulkInsertVotes(
            userId = user.id!!,
            pollId = poll.id!!,
            optionIds = request.options.toTypedArray()
        )

        entityManager.flush()
        entityManager.clear()

        val result = pollRepository.findById(pollId).orElseThrow { throw NotFoundException("Poll not found") }
        return PollResponse.fromPoll(result)
    }

    @Throws(BadRequestException::class)
    private fun validateVote(user: User, poll: Poll, request: VotePollRequest) {
        if (poll.status != PollStatus.OPEN) {
            throw BadRequestException("Poll is closed")
        }

        val pollOptionIds = poll.options.map { it.id }.toSet()
        if (!request.options.all { it in pollOptionIds }) {
            throw BadRequestException("Invalid options selected")
        }

        val existingVotes = voteRepository.findAllByPollAndUser(poll, user)
        val hasVoted = existingVotes.isNotEmpty()

        if (hasVoted && poll.type != PollType.MULTIPLE_CHOICE) {
            throw BadRequestException("You have already voted")
        }

        when (poll.type) {
            PollType.SINGLE_CHOICE, PollType.QUIZ -> {
                if (request.options.size != 1) {
                    throw BadRequestException("Exactly one option required")
                }
            }

            PollType.MULTIPLE_CHOICE -> {
                if (request.options.isEmpty()) {
                    throw BadRequestException("At least one option must be selected")
                }

                val alreadySelectedOptionIds = existingVotes.map { it.option.id }.toSet()
                val duplicateVotes = request.options.intersect(alreadySelectedOptionIds)
                if (duplicateVotes.isNotEmpty()) {
                    throw BadRequestException("You have already voted for one or more of these options")
                }
            }
        }
    }

    @Transactional
    fun retractVote(user: User, pollId: UUID) {
        val deletedCount = voteRepository.deleteAllByPollIdAndUserId(pollId, user.id!!)
        if (deletedCount == 0) {
            throw NotFoundException("Vote not found")
        }
    }

    @Transactional
    fun deletePoll(user: User, pollId: UUID) {
        val poll = pollRepository.findByIdAndCreatedBy(pollId, user) ?: throw NotFoundException("Poll not found")
        pollRepository.delete(poll)
    }
}