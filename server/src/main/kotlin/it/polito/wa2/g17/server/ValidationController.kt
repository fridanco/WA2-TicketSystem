package it.polito.wa2.g17.server

import io.jsonwebtoken.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.security.Key
import java.util.*
import javax.crypto.spec.SecretKeySpec


//Encode JWT with this secret and select Base64 encoded
var secret = "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4"
var hmacKey: Key = SecretKeySpec(Base64.getDecoder().decode(secret),SignatureAlgorithm.HS256.jcaName)


@RestController
class ValidationController {
    @PostMapping("/validate")
    fun responseValidate(  @RequestBody validationObject: ValidationObject) : ResponseEntity<Any> {

        var validatedJwt : Jws<Claims>

        //JWT validation
        try {
            validatedJwt = Jwts
                .parserBuilder()
                .setSigningKey(hmacKey)
                .build()
                .parseClaimsJws(validationObject.token)
            println("--valid token--")
        } catch (e: JwtException) {
            println("invalid token: ${e.message}")
            return ResponseEntity("",HttpStatus.FORBIDDEN)
        }

        //Get JWT body (payload) claims
        val expiration = validatedJwt.body["exp"]
        val validityZone = validatedJwt.body["vz"]
        println("expiration: $expiration")
        println("validity zones: $validityZone")

        //insert checks
        //expiration time check already done by jjwt at validation

        //Return response with empty body & code 200
        return ResponseEntity("",HttpStatus.OK)
    }
}