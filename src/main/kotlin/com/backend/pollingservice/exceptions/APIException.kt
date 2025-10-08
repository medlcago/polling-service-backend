package com.backend.pollingservice.exceptions

import org.springframework.http.HttpStatus

open class APIException(
    message: String? = null,
    val errorCode: String,
    val httpStatus: HttpStatus,
) : RuntimeException(message ?: httpStatus.reasonPhrase)