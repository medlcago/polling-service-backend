package com.backend.pollingservice.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

data class ApiResponse<out T>(
    val ok: Boolean,

    @get:JsonProperty("error_code")
    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    val errorCode: String? = null,

    val data: T? = null,

    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    val message: String? = null,

    @get:JsonInclude(JsonInclude.Include.NON_NULL)
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
