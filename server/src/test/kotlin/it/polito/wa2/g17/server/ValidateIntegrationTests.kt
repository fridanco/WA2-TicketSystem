package it.polito.wa2.g17.server

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import it.polito.wa2.g17.server.dtos.TicketDTO
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import java.security.Key
import java.util.*
import javax.crypto.spec.SecretKeySpec

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ValidateIntegrationTests : InitializingBean {
    @LocalServerPort
    protected var port: Int = 0

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Value("\${server.jwt.secretkey}")
    lateinit var secret : String

    lateinit var hmacKey: Key

    lateinit var expiredJWT : String
    lateinit var validJWT : String
    lateinit var validJWT_withDB_1 : String
    lateinit var validJWT_withDB_2 : String
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
            .setClaims(mapOf("vz" to "123", "sub" to "aaaaaaaaabbbbbaaaaaaaaaaa"))
            .setExpiration(Date(System.currentTimeMillis() + 60000))
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

    var invalidSignatureJWT: String = Jwts
        .builder()
        .signWith(Keys.secretKeyFor(SignatureAlgorithm.HS256))      //use a random key
        .compact()


    @Test
    fun rejectInvalidJWT() {
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(TicketDTO("1",invalidSignatureJWT))
        val response = restTemplate.postForEntity<Unit>(
            "$baseUrl/validate",
            request )
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun rejectExpiredJWT() {
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(TicketDTO("1",expiredJWT))
        val response = restTemplate.postForEntity<Unit>(
            "$baseUrl/validate",
            request )
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun rejectInvalidValidityZone() {
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(TicketDTO("1",validEmptyZonesJWT))
        val response = restTemplate.postForEntity<Unit>(
            "$baseUrl/validate",
            request )
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun rejectEmptyZone() {
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(TicketDTO("",validEmptyZonesJWT))
        val response = restTemplate.postForEntity<Unit>(
            "$baseUrl/validate",
            request )
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun rejectEmptyJWT() {
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(TicketDTO("1",""))
        val response = restTemplate.postForEntity<Unit>(
            "$baseUrl/validate",
            request )
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun rejectEmptyRequest() {
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(TicketDTO("",""))
        val response = restTemplate.postForEntity<Unit>(
            "$baseUrl/validate",
            request )
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun acceptValidJWT() {
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(TicketDTO("1",validJWT))
        val response = restTemplate.postForEntity<Unit>(
            "$baseUrl/validate",
            request )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun acceptUniqueTicket() {
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(TicketDTO("1",validJWT_withDB_1))
        val response = restTemplate.postForEntity<Unit>(
            "$baseUrl/validate",
            request )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun rejectDuplicateTicket() {
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(TicketDTO("1",validJWT_withDB_2))
        val response = restTemplate.postForEntity<Unit>(
            "$baseUrl/validate",
            request )
        val request1 = HttpEntity(TicketDTO("1",validJWT_withDB_2))
        val response1 = restTemplate.postForEntity<Unit>(
            "$baseUrl/validate",
            request1 )
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response1.statusCode)
    }
}
