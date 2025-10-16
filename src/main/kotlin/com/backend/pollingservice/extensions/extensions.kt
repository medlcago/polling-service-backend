package com.backend.pollingservice.extensions

import com.backend.pollingservice.dto.ApiResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

fun Any.toJsonString(): String = ObjectMapper().writeValueAsString(this)


fun HttpServletResponse.sendJsonResponse(
    status: HttpStatus = HttpStatus.OK,
    contentType: String = MediaType.APPLICATION_JSON_VALUE,
    data: Any
) {
    this.status = status.value()
    this.contentType = contentType
    this.writer.write(data.toJsonString())
}

fun HttpServletResponse.unauthorized(message: String? = null) = sendJsonResponse(
    status = HttpStatus.UNAUTHORIZED,
    data = ApiResponse.error(
        message = message ?: HttpStatus.UNAUTHORIZED.reasonPhrase,
        errorCode = HttpStatus.UNAUTHORIZED.name
    )
)

fun HttpServletResponse.forbidden(message: String? = null) = sendJsonResponse(
    status = HttpStatus.FORBIDDEN,
    data = ApiResponse.error(
        message = message ?: HttpStatus.FORBIDDEN.reasonPhrase,
        errorCode = HttpStatus.FORBIDDEN.name
    )
)