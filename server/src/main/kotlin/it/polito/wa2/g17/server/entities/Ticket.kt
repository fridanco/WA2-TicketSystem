package it.polito.wa2.g17.server.entities

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
open class Ticket (
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    open var id: String? = null
)