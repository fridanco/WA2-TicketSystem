package it.polito.wa2.g17.server

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.security.Key
import java.util.*
import javax.crypto.spec.SecretKeySpec

@SpringBootTest
class ValidateUnitTests {

    @Autowired
    lateinit var validationController : ValidationController

    final val secret = "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4"
    final val hmacKey: Key = SecretKeySpec(Base64.getDecoder().decode(secret), SignatureAlgorithm.HS256.jcaName)

    var invalidSignatureJWT = Jwts
        .builder()
        .signWith(Keys.secretKeyFor(SignatureAlgorithm.HS256))      //use a random key
        .compact()

    var expiredJWT = Jwts
        .builder()
        .setClaims(mapOf("vz" to "1,2,3", "sub" to Math.random().toInt().toString()))
        .setExpiration(Date())
        .signWith(hmacKey)      //use a random key
        .compact()

    var validJWT = Jwts
        .builder()
        .setClaims(mapOf("vz" to "1,2,3", "sub" to Math.random().toInt().toString()))
        .setExpiration(Date(System.currentTimeMillis()+60000))
        .signWith(hmacKey)      //use a random key
        .compact()

    var validEmptyZonesJWT = Jwts
        .builder()
        .setClaims(mapOf("vz" to "", "sub" to Math.random().toInt().toString()))
        .setExpiration(Date(System.currentTimeMillis()+60000))
        .signWith(hmacKey)      //use a random key
        .compact()

    @Test
    fun rejectInvalidJWT(){

        val ticketDTO = TicketDTO("1", invalidSignatureJWT)
        //Assertions.assertEquals(403, validationController.responseValidate(ticketDTO).statusCodeValue)
        Assertions.assertThrows(JwtException::class.java){
            validationController.responseValidate(ticketDTO)
        }
    }

    @Test
    fun rejectExpiredJWT(){
        val ticketDTO = TicketDTO("1", expiredJWT)
        //Assertions.assertEquals(403, validationController.responseValidate(ticketDTO).statusCodeValue)
        //When checking validity with jjwt, the token expiration is also verified automatically
        Assertions.assertThrows(JwtException::class.java){
            validationController.responseValidate(ticketDTO)
        }
    }

    @Test
    fun rejectInvalidValidityZone(){
        val ticketDTO = TicketDTO("A",validJWT)
        Assertions.assertThrows(InvalidZoneException::class.java){
            validationController.responseValidate(ticketDTO)
        }
    }

    @Test
    fun rejectEmptyValidityZoneToken(){
        val ticketDTO = TicketDTO("1",validEmptyZonesJWT)
        Assertions.assertThrows(InvalidZoneException::class.java){
            validationController.responseValidate(ticketDTO)
        }
    }

    @Test
    fun acceptValidJWT(){
        val ticketDTO = TicketDTO("1", validJWT)
        Assertions.assertDoesNotThrow { validationController.responseValidate(ticketDTO) }
    }
}