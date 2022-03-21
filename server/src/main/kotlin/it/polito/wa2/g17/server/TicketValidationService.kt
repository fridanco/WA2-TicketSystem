package it.polito.wa2.g17.server

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.Key
import java.util.*
import javax.crypto.spec.SecretKeySpec

@Service
class TicketValidationService : InitializingBean {

    //Encode JWT with this secret and select Base64 encoded
    @Value("\${server.jwt.secretkey}")
    lateinit var secret : String

    lateinit var hmacKey : Key

    override fun afterPropertiesSet() {
        hmacKey = SecretKeySpec(Base64.getDecoder().decode(secret), SignatureAlgorithm.HS256.jcaName)
    }

    fun validateTicket(zone: String, token: String){

        val validatedJwt : Jws<Claims>

        //JWT validation
        validatedJwt = Jwts
            .parserBuilder()
            .setSigningKey(hmacKey)
            .build()
            .parseClaimsJws(token)

        //Get token expiration from JWT body (payload) claims & perform checks
        val expiration = validatedJwt.body.expiration
        if(expiration==null || expiration.before(Date())){
            throw ExpiredJwtException()
        }

        //Get ticket vz from JWT body (payload) claims & perform checks
        var validTicketVZ = false
        val validityZones = validatedJwt.body.get("vz",String::class.java)?.forEach {
            if(it==zone.toCharArray()[0]){
                validTicketVZ=true
            }
        }
        if(!validTicketVZ){
            throw InvalidZoneException()
        }

    }
}