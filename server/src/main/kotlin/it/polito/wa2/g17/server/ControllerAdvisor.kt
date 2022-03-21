package it.polito.wa2.g17.server

import io.jsonwebtoken.JwtException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ControllerAdvisor {

    @ExceptionHandler(JwtException::class)
    fun handleJwtException(e: JwtException) : ResponseEntity<Any> {
        println(e.message)
        return ResponseEntity("",HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(ExpiredJwtException::class)
    fun handleExpiredJWTException(e: ExpiredJwtException) : ResponseEntity<Any> {
        println(e.message)
        return ResponseEntity("",HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(InvalidZoneException::class)
    fun handleInvalidZoneException(e: InvalidZoneException) : ResponseEntity<Any> {
        println(e.message)
        return ResponseEntity("",HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(InvalidRequestBodyException::class)
    fun handleInvalidRequestBodyException(e: InvalidRequestBodyException) : ResponseEntity<Any> {
        println(e.message)
        return ResponseEntity("",HttpStatus.FORBIDDEN)
    }
}