package it.polito.wa2.g17.server.services

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import it.polito.wa2.g17.server.entities.Ticket
import it.polito.wa2.g17.server.exceptions.DuplicateTicketException
import it.polito.wa2.g17.server.exceptions.ExpiredJwtException
import it.polito.wa2.g17.server.exceptions.InvalidZoneException
import it.polito.wa2.g17.server.repositories.TicketRepository
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
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

    @Autowired
    lateinit var ticketRepository: TicketRepository

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
        validatedJwt.body.get("vz",String::class.java)?.forEach {
            if(it==zone.toCharArray()[0]){
                validTicketVZ=true
            }
        }
        if(!validTicketVZ){
            throw InvalidZoneException()
        }

        //Check ticket unicity (check in DB)
        val ticketID = validatedJwt?.body?.get("sub",String::class.java)
        if(ticketID!=null && ticketID.isNotEmpty()){
            try {
                ticketRepository.save(Ticket().apply {
                    id = ticketID
                })
            }
            catch (ex: Exception){
                throw DuplicateTicketException();
            }

        }
    }



}