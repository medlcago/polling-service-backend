package com.backend.pollingservice.exceptions

import org.springframework.http.HttpStatus

class BadRequestException(message: String? = null) : APIException(
    message = message,
    errorCode = HttpStatus.BAD_REQUEST.name,
    httpStatus = HttpStatus.BAD_REQUEST,
)