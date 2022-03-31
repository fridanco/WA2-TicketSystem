package it.polito.wa2.g17.server.repositories;

import it.polito.wa2.g17.server.entities.Ticket
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TicketRepository : CrudRepository<Ticket, String> {
}