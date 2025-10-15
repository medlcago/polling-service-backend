package com.backend.pollingservice.helpers

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

object PageableHelper {
    fun createPageable(offset: Int, limit: Int): Pageable {
        require(offset >= 0) { "Offset must be non-negative" }
        require(limit > 0) { "Limit must be positive" }

        return PageRequest.of(calculatePageNumber(offset, limit), limit)
    }

    fun createPageable(offset: Int, limit: Int, sort: Sort): Pageable {
        require(offset >= 0) { "Offset must be non-negative" }
        require(limit > 0) { "Limit must be positive" }

        return PageRequest.of(calculatePageNumber(offset, limit), limit, sort)
    }

    fun createPageable(offset: Int, limit: Int, direction: Sort.Direction, properties: String): Pageable {
        return createPageable(offset, limit, Sort.by(direction, properties))
    }

    private fun calculatePageNumber(offset: Int, limit: Int): Int {
        return offset / limit
    }
}