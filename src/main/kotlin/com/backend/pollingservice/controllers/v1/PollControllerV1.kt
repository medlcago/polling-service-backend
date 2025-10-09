package com.backend.pollingservice.controllers.v1

import com.backend.pollingservice.dto.ApiResponse
import com.backend.pollingservice.dto.CreatePollRequest
import com.backend.pollingservice.dto.PollResponse
import com.backend.pollingservice.services.PollService
import com.backend.pollingservice.user.UserDetails
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/polls")
class PollControllerV1(
    private val pollService: PollService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createPoll(
        @Valid @RequestBody request: CreatePollRequest,
        @AuthenticationPrincipal user: UserDetails,
    ): ApiResponse<PollResponse> {
        val poll = pollService.createPoll(
            user = user.getUser(),
            request = request,
        )
        return ApiResponse.success(poll)
    }

    @GetMapping
    fun getPolls(): ApiResponse<List<PollResponse>> {
        val polls = pollService.getPolls()
        return ApiResponse.success(polls)
    }

    @GetMapping("/{pollId}")
    fun getPoll(@PathVariable pollId: UUID): ApiResponse<PollResponse> {
        val poll = pollService.getPoll(pollId)
        return ApiResponse.success(poll)
    }
}