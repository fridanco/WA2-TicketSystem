package it.polito.wa2.g17.server

import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.SigningKeyResolver
import io.jsonwebtoken.security.Keys
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.security.Key


val key: Key = Keys.secretKeyFor(SignatureAlgorithm.HS256)


@RestController
class ValidationController {
    @PostMapping("/validate")
    fun responseValidate(  @RequestBody validationObject: ValidationObject) : ResponseEntity<Any> {
        //Print the received object
        println(validationObject.zone)
        println(validationObject.token)

        //JWT operations
        try {

            //OK, we can trust this JWT
        } catch (e: JwtException) {
            println("invalid token")
            return ResponseEntity("",HttpStatus.FORBIDDEN)
        }


        //Return response with empty body & code 200
        return ResponseEntity("",HttpStatus.OK)
    }
}