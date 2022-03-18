package it.polito.wa2.g17.server

import org.apache.el.util.Validation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestController
class ValidationController {
    @PostMapping("/validate")
    fun responseValidate(  @RequestBody body: ValidationObject): String? {
        println(body);
        return "$body.zone";
    }
}