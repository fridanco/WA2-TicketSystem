package it.polito.wa2.g17.server

import it.polito.wa2.g17.server.entities.Ticket
import it.polito.wa2.g17.server.repositories.TicketRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

//@SpringBootApplication
//class ServerApplication{
//    @Bean
//    fun showDbIsWorking(ticketRepository: TicketRepository): CommandLineRunner{
//        return CommandLineRunner {
//            ticketRepository.save(Ticket().apply {
//                id = "CiaoMarioSeiUNoStronzo"
//            })
//        }
//    }
//    fun main(args: Array<String>) {
//        runApplication<ServerApplication>(*args)
//    }
//}

@SpringBootApplication
class ServerApplication

fun main(args: Array<String>) {
    runApplication<ServerApplication>(*args)
}


