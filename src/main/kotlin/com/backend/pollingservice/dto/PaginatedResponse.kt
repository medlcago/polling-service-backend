package com.backend.pollingservice.dto

data class PaginatedResponse<out T>(
    val total: Long,
    val result: List<T>
)
