package com.backend.pollingservice.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

data class ApiResponse<T>(
    val ok: Boolean,

    @field:JsonProperty("error_code")
    val errorCode: String? = null,

    val data: T? = null,

    val message: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    val errors: Map<String, String>? = null
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> = ApiResponse(ok = true, data = data)
        fun error(
            message: String? = null,
            errors: Map<String, String>? = null,
            errorCode: String
        ): ApiResponse<Nothing> =
            ApiResponse(ok = false, message = message, errors = errors, errorCode = errorCode)
    }
}
