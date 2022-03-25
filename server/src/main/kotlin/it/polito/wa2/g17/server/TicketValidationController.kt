package it.polito.wa2.g17.server

import io.jsonwebtoken.*
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.security.Key
import java.util.*
import javax.crypto.spec.SecretKeySpec
import javax.validation.Valid


@RestController

class ValidationController {

    @Autowired
    lateinit var ticketValidationService : TicketValidationService

    @PostMapping("/validate")
    @ResponseStatus(HttpStatus.OK)
    fun responseValidate(@RequestBody @Valid ticketDTO: TicketDTO, br : BindingResult) {

        //Request body validation
        if(br.hasErrors()){
            throw InvalidRequestBodyException()
        }

        ticketValidationService.validateTicket(ticketDTO.zone, ticketDTO.token)

    }

}