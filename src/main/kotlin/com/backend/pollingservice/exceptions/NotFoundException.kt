package com.backend.pollingservice.exceptions

import org.springframework.http.HttpStatus

class NotFoundException(
    message: String? = null,
) : APIException(
    message = message,
    errorCode = HttpStatus.NOT_FOUND.name,
    httpStatus = HttpStatus.NOT_FOUND,
)