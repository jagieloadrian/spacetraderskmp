package com.anjo.spacetradersservice.exception.handler

import com.anjo.spacetradersservice.exception.NonSpaceTraderValidException
import com.anjo.spacetradersservice.exception.NonValidActionException
import com.anjo.spacetradersservice.exception.NotFoundTokenException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.reactive.function.client.WebClientResponseException.NotFound

@ControllerAdvice
class ExceptionControllerAdvice {

    @ExceptionHandler(NonSpaceTraderValidException::class)
    suspend fun handleNonValidException(nonValidExc: NonSpaceTraderValidException): ResponseEntity<ErrorMessageModel> {
        val errorResponse = ErrorMessageModel(nonValidExc.message ?: "", HttpStatus.BAD_REQUEST.value())
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(NonValidActionException::class)
    suspend fun handleNonValidActionException(nonValidExc: NonValidActionException): ResponseEntity<ErrorMessageModel> {
        val errorResponse = ErrorMessageModel(nonValidExc.message ?: "", HttpStatus.BAD_REQUEST.value())
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(NotFoundTokenException::class)
    suspend fun handleNonValidException(notFoundExc: NotFoundTokenException): ResponseEntity<ErrorMessageModel> {
        val errorResponse = ErrorMessageModel(notFoundExc.message ?: "", HttpStatus.NOT_FOUND.value())
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(NotFound::class)
    suspend fun handleNotFound(notFoundExc: NotFound): ResponseEntity<ErrorMessageModel> {
        val errorResponse = ErrorMessageModel(notFoundExc.message ?: "", HttpStatus.NOT_FOUND.value())
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }
}

data class ErrorMessageModel(val errorMessage: String, val httpStatusCode: Int)
