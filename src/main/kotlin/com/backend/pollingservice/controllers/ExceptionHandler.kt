package com.backend.pollingservice.controllers

import com.backend.pollingservice.dto.ApiResponse
import com.backend.pollingservice.exceptions.APIException
import org.springframework.beans.TypeMismatchException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.resource.NoResourceFoundException

@ControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(APIException::class)
    fun handleAPIException(exp: APIException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.status(exp.httpStatus).body(
            ApiResponse.error(
                message = exp.message,
                errorCode = exp.errorCode,
            )
        )
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFoundException(): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ApiResponse.error(
                message = HttpStatus.NOT_FOUND.reasonPhrase,
                errorCode = HttpStatus.NOT_FOUND.name
            )
        )
    }

    @ExceptionHandler(TypeMismatchException::class)
    fun handleMethodArgumentTypeMismatchException(): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.badRequest().body(
            ApiResponse.error(
                message = HttpStatus.BAD_REQUEST.reasonPhrase,
                errorCode = HttpStatus.BAD_REQUEST.name,
            )
        )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.badRequest().body(
            ApiResponse.error(
                message = HttpStatus.BAD_REQUEST.reasonPhrase,
                errorCode = HttpStatus.BAD_REQUEST.name,
            )
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(exp: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Nothing>> {
        val errors = exp.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "Invalid value")
        }

        return ResponseEntity.badRequest().body(
            ApiResponse.error(
                message = HttpStatus.BAD_REQUEST.reasonPhrase,
                errorCode = HttpStatus.BAD_REQUEST.name,
                errors = errors
            )
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleException(): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.internalServerError().body(
            ApiResponse.error(
                message = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
                errorCode = HttpStatus.INTERNAL_SERVER_ERROR.name,
            )
        )
    }
}