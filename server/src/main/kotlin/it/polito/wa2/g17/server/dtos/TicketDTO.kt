package it.polito.wa2.g17.server.dtos

import javax.validation.constraints.Size

data class TicketDTO (
    @field:Size(min = 1)
    var zone: String,
    @field:Size(min = 1)
    var token: String)