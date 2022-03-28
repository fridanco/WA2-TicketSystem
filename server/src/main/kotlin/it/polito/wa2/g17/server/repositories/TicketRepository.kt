package it.polito.wa2.g17.server.repositories;

import it.polito.wa2.g17.server.entities.Ticket
import org.springframework.data.jpa.repository.JpaRepository

interface TicketRepository : JpaRepository<Ticket, String> {
}