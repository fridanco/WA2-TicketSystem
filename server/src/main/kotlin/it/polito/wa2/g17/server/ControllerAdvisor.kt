package it.polito.wa2.g17.server

import io.jsonwebtoken.JwtException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ControllerAdvisor {

    @ExceptionHandler(JwtException::class)
    fun handleJwtException() : ResponseEntity<Any> {
        println("JWT is invalid")
        return ResponseEntity("",HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(ExpiredJwtException::class)
    fun handleExpiredJWTException() : ResponseEntity<Any> {
        println("JWT expired")
        return ResponseEntity("",HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(InvalidZoneException::class)
    fun handleInvalidZoneException() : ResponseEntity<Any> {
        println("Invalid ticket validity zone")
        return ResponseEntity("",HttpStatus.FORBIDDEN)
    }
}