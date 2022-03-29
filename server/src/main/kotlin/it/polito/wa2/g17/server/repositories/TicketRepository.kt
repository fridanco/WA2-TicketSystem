package it.polito.wa2.g17.server.repositories;

import it.polito.wa2.g17.server.entities.Ticket
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository

interface TicketRepository : CrudRepository<Ticket, String> {
}