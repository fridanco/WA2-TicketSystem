package it.polito.wa2.g17.server

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestController
class ValidationController {
    @PostMapping("/validate")
    fun helloWorld():String?{
        return "sprint iniziamo male"
    }
}