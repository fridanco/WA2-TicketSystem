package it.polito.wa2.g17.server

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import it.polito.wa2.g17.server.exceptions.DuplicateTicketException
import it.polito.wa2.g17.server.exceptions.InvalidZoneException
import it.polito.wa2.g17.server.repositories.TicketRepository
import it.polito.wa2.g17.server.services.TicketValidationService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import java.security.Key
import java.util.*
import javax.crypto.spec.SecretKeySpec

@SpringBootTest
class ValidateUnitTests : InitializingBean {

    @Autowired
    lateinit var ticketValidationService: TicketValidationService

    @Autowired
    lateinit var ticketRepository: TicketRepository

    @Value("\${server.jwt.secretkey}")
    lateinit var secret : String

    lateinit var hmacKey: Key

    lateinit var expiredJWT : String
    lateinit var validJWT : String
    lateinit var validJWT_withDB_1: String
    lateinit var validJWT_withDB_2: String
    lateinit var validEmptyZonesJWT : String

    override fun afterPropertiesSet() {
        hmacKey = SecretKeySpec(Base64.getDecoder().decode(secret), SignatureAlgorithm.HS256.jcaName)

        expiredJWT = Jwts
            .builder()
            .setClaims(mapOf("vz" to "123", "sub" to ""))
            .setExpiration(Date())
            .signWith(hmacKey)      //use a random key
            .compact()

        validJWT = Jwts
            .builder()
            .setClaims(mapOf("vz" to "123", "sub" to ""))
            .setExpiration(Date(System.currentTimeMillis()+60000))
            .signWith(hmacKey)      //use a random key
            .compact()

        validJWT_withDB_1 = Jwts
            .builder()
            .setClaims(mapOf("vz" to "123", "sub" to "aaaaaaaaabbbbbasdaaaaaaaaaaa"))
            .setExpiration(Date(System.currentTimeMillis()+60000))
            .signWith(hmacKey)      //use a random key
            .compact()

        validJWT_withDB_2 = Jwts
            .builder()
            .setClaims(mapOf("vz" to "123", "sub" to "984587126354z78432657823356"))
            .setExpiration(Date(System.currentTimeMillis() + 60000))
            .signWith(hmacKey)      //use a random key
            .compact()

        validEmptyZonesJWT = Jwts
            .builder()
            .setClaims(mapOf("vz" to "", "sub" to ""))
            .setExpiration(Date(System.currentTimeMillis()+60000))
            .signWith(hmacKey)      //use a random key
            .compact()
    }

    //todo: perchè fuori da afterPropertiesSet?
    var invalidSignatureJWT: String = Jwts
        .builder()
        .signWith(Keys.secretKeyFor(SignatureAlgorithm.HS256))      //use a random key
        .compact()

    
    @Test
        fun rejectInvalidJWT(){
        Assertions.assertThrows(JwtException::class.java){
            ticketValidationService.validateTicket("1",invalidSignatureJWT)
        }
    }

    @Test
    fun rejectExpiredJWT(){
        //When checking validity with jjwt, the token expiration is also verified automatically
        Assertions.assertThrows(JwtException::class.java){
            ticketValidationService.validateTicket("1",expiredJWT)
        }
    }

    @Test
    fun rejectInvalidValidityZone(){
        Assertions.assertThrows(InvalidZoneException::class.java){
            ticketValidationService.validateTicket("A",validJWT)
        }
    }

    @Test

    fun rejectEmptyValidityZoneToken(){
        Assertions.assertThrows(InvalidZoneException::class.java){
            ticketValidationService.validateTicket("1",validEmptyZonesJWT)
        }
    }

    @Test
    fun acceptValidJWT(){
        Assertions.assertDoesNotThrow {
            ticketValidationService.validateTicket("1",validJWT)
        }
    }

    @Test
    fun acceptUniqueTicket(){
        Assertions.assertDoesNotThrow{
            ticketValidationService.validateTicket("1",validJWT_withDB_1)
        }
    }
    @Test
    fun rejectDuplicateTicket(){
        Assertions.assertThrows(DuplicateTicketException::class.java){
            ticketValidationService.validateTicket("1",validJWT_withDB_2)
            ticketValidationService.validateTicket("1",validJWT_withDB_2)
        }
    }
}
