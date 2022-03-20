package it.polito.wa2.g17.server

import io.jsonwebtoken.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
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
    @ResponseStatus(HttpStatus.OK)
    fun responseValidate(@RequestBody ticketDTO: TicketDTO) {

        val validatedJwt : Jws<Claims>

        //JWT validation
        validatedJwt = Jwts
                .parserBuilder()
                .setSigningKey(hmacKey)
                .build()
                .parseClaimsJws(ticketDTO.token)

        //Get token expiration from JWT body (payload) claims & perform checks
        val expiration = validatedJwt.body.expiration
        if(expiration==null || expiration.before(Date())){
            throw ExpiredJwtException()
            //return ResponseEntity("",HttpStatus.FORBIDDEN)
        }

        //Get ticket vz from JWT body (payload) claims & perform checks
        val validityZonesArray = validatedJwt.body.get("vz",String::class.java)?.split(",")
        if(validityZonesArray == null || validityZonesArray.find { it==ticketDTO.zone } == null){
            throw InvalidZoneException()
            //return ResponseEntity("",HttpStatus.FORBIDDEN)
        }
    }

}