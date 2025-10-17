package com.backend.pollingservice.controllers.v1

import com.backend.pollingservice.dto.*
import com.backend.pollingservice.security.UserDetails
import com.backend.pollingservice.services.PollService
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/polls")
@Validated
class PollControllerV1(
    private val pollService: PollService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createPoll(
        @Valid @RequestBody request: CreatePollRequest,
        @AuthenticationPrincipal user: UserDetails,
    ): ApiResponse<PollResponseDTO> {
        val poll = pollService.createPoll(
            user = user.getUser(),
            request = request,
        )
        return ApiResponse.success(poll)
    }

    @GetMapping
    fun getPolls(
        @RequestParam("limit", defaultValue = "10") @Min(1) @Max(100) limit: Int,
        @RequestParam("offset", defaultValue = "0") @Min(0) offset: Int,
        @AuthenticationPrincipal user: UserDetails?,
    ): ApiResponse<PaginatedResponse<PollResponseDTO>> {
        val polls = pollService.getPolls(limit, offset, user?.getUser())
        return ApiResponse.success(polls)
    }

    @GetMapping("/{pollId}")
    fun getPoll(
        @PathVariable pollId: UUID,
        @AuthenticationPrincipal user: UserDetails?
    ): ApiResponse<PollResponseDTO> {
        val poll = pollService.getPoll(pollId, user?.getUser())
        return ApiResponse.success(poll)
    }

    @PostMapping("/{pollId}/close")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun closePoll(
        @PathVariable pollId: UUID,
        @AuthenticationPrincipal user: UserDetails,
    ) {
        pollService.closePoll(user.getUser(), pollId)
    }

    @PostMapping("/{pollId}/vote")
    fun votePoll(
        @PathVariable pollId: UUID,
        @AuthenticationPrincipal user: UserDetails,
        @Valid @RequestBody request: VotePollRequest,
    ): ApiResponse<VotePollResponseDTO> {
        val vote = pollService.votePoll(user.getUser(), pollId, request)
        return ApiResponse.success(vote)
    }

    @PostMapping("/{pollId}/retract")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun retractVote(
        @PathVariable pollId: UUID,
        @AuthenticationPrincipal user: UserDetails,
    ) {
        pollService.retractVote(user.getUser(), pollId)
    }

    @DeleteMapping("/{pollId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletePoll(
        @PathVariable pollId: UUID,
        @AuthenticationPrincipal user: UserDetails
    ) {
        pollService.deletePoll(user.getUser(), pollId)
    }

    @GetMapping("/{pollId}/members")
    fun getPollMembers(
        @PathVariable pollId: UUID,
        @RequestParam("limit", defaultValue = "10") @Min(1) @Max(100) limit: Int,
        @RequestParam("offset", defaultValue = "0") @Min(0) offset: Int,
    ): ApiResponse<PaginatedResponse<PollMemberResponseDTO>> {
        val members = pollService.getPollMembers(pollId, limit, offset)
        return ApiResponse.success(members)
    }
}