package com.backend.pollingservice.services

import com.backend.pollingservice.dto.*
import com.backend.pollingservice.entities.Poll
import com.backend.pollingservice.entities.User
import com.backend.pollingservice.enums.PollStatus
import com.backend.pollingservice.enums.PollType
import com.backend.pollingservice.exceptions.BadRequestException
import com.backend.pollingservice.exceptions.NotFoundException
import com.backend.pollingservice.helpers.PageableHelper
import com.backend.pollingservice.mappers.toDetailedResponse
import com.backend.pollingservice.mappers.toPublicResponse
import com.backend.pollingservice.repositories.PollRepository
import com.backend.pollingservice.repositories.VoteRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class PollService(
    private val pollRepository: PollRepository,
    private val voteRepository: VoteRepository,
) {
    @Throws(BadRequestException::class)
    fun createPoll(user: User, request: CreatePollRequest): PollResponseDTO {
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
        return result.toDetailedResponse(emptyList())
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

    fun getPolls(limit: Int, offset: Int, user: User? = null): PaginatedResponse<PollResponseDTO> {
        val pageable = PageableHelper.createPageable(offset, limit, Sort.by(Sort.Direction.DESC, "createdAt"))

        val pollsPage = pollRepository.findAll(pageable)
        val polls = pollsPage.content

        val userVotesMap = getUserVotesMap(polls, user)

        val result = polls.map {
            val selectedOptionIds = userVotesMap[it.id].orEmpty()
            if (it.isCreatedBy(user)) {
                it.toDetailedResponse(selectedOptionIds)
            } else {
                it.toPublicResponse(selectedOptionIds)
            }
        }

        return PaginatedResponse(
            total = pollsPage.totalElements,
            result = result
        )
    }

    @Throws(NotFoundException::class)
    fun getPoll(id: UUID, user: User? = null): PollResponseDTO {
        val poll = pollRepository.findById(id).orElseThrow { NotFoundException("Poll not found") }

        val userVotesMap = getUserVotesMap(listOf(poll), user)
        val selectedOptionIds = userVotesMap[poll.id].orEmpty()

        return if (poll.isCreatedBy(user)) {
            poll.toDetailedResponse(selectedOptionIds)
        } else {
            poll.toPublicResponse(selectedOptionIds)
        }
    }


    private fun getUserVotesMap(
        polls: List<Poll>,
        user: User?
    ): Map<UUID, List<UUID>> {
        if (polls.isEmpty()) return emptyMap()

        val pollIds = polls.mapNotNull { it.id }

        return if (user != null) {
            voteRepository.findViewByPollIdsAndUserId(pollIds, user.id!!)
                .groupBy({ it.pollId }, { it.optionId })
        } else {
            emptyMap()
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
    fun votePoll(user: User, pollId: UUID, request: VotePollRequest): VotePollResponseDTO {
        val poll = pollRepository.findById(pollId).orElseThrow { throw NotFoundException("Poll not found") }
        validateVote(user, poll, request)

        voteRepository.bulkInsertVotes(
            userId = user.id!!,
            optionIds = request.options.toTypedArray()
        )

        if (poll.type != PollType.QUIZ) {
            return VotePollResponseDTO(id = pollId, isQuiz = false, isCorrect = null)
        }

        val correctOption = poll.options.first { it.isCorrect == true }.id
        val selectedOption = request.options.first()
        val isCorrect = selectedOption == correctOption
        return VotePollResponseDTO(id = pollId, isQuiz = true, isCorrect = isCorrect)
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

        val existingVotes = voteRepository.findAllByPollIdAndUserId(poll.id!!, user.id!!)
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
        val votes = voteRepository.findAllByPollIdAndUserId(pollId, user.id!!)
            .ifEmpty { throw NotFoundException("Vote not found") }
        voteRepository.deleteAllInBatch(votes)
    }

    @Transactional
    fun deletePoll(user: User, pollId: UUID) {
        val poll = pollRepository.findByIdAndCreatedBy(pollId, user) ?: throw NotFoundException("Poll not found")
        pollRepository.delete(poll)
    }

    fun getPollMembers(pollId: UUID, limit: Int, offset: Int): PaginatedResponse<PollMemberResponseDTO> {
        val isAnonymous = pollRepository.isAnonymous(pollId)
        if (isAnonymous == null || isAnonymous) {
            return PaginatedResponse(
                total = 0,
                result = emptyList(),
            )
        }

        val pageable = PageableHelper.createPageable(offset, limit)
        val membersPage = voteRepository.findAllMembers(pollId, pageable)

        return PaginatedResponse(
            total = membersPage.totalElements,
            result = membersPage.content,
        )
    }
}