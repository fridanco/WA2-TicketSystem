package it.polito.wa2.g17.server

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
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
class ValidateIntegrationTests() {
    @LocalServerPort
    protected var port: Int = 0

    @Autowired
    lateinit var restTemplate: TestRestTemplate

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
    fun acceptValidJWT() {
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(TicketDTO("1",validJWT))
        val response = restTemplate.postForEntity<Unit>(
            "$baseUrl/validate",
            request )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
    }
}
