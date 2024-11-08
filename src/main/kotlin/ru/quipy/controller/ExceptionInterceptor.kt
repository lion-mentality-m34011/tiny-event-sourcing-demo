package ru.quipy.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import javassist.NotFoundException

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException::class)
    fun handleStateException(ex: IllegalStateException): ResponseEntity<ErrorDetails> {
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleArgumentException(ex: IllegalArgumentException): ResponseEntity<ErrorDetails> {
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex)
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleResourceNotFoundException(ex: NotFoundException): ResponseEntity<ErrorDetails> {
        return createErrorResponse(HttpStatus.NOT_FOUND, ex)
    }

    private fun createErrorResponse(status: HttpStatus, exception: Exception): ResponseEntity<ErrorDetails> {
        val error = ErrorDetails(status.value(), exception.message ?: "Unknown error")
        return ResponseEntity.status(status).body(error)
    }
}

data class ErrorDetails(
    val code: Int,
    val description: String
)