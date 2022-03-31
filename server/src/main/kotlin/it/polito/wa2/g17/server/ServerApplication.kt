package it.polito.wa2.g17.server

import it.polito.wa2.g17.server.entities.Ticket
import it.polito.wa2.g17.server.repositories.TicketRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class ServerApplication {
    @Bean
    fun showDbIsWorking(ticketRepository: TicketRepository): CommandLineRunner{
        return CommandLineRunner {

            if(!ticketRepository.findById("CiaoMarioSeiUNoStronzo").isEmpty){
                println("presente")
            }
            if(ticketRepository.findById("CiaoMarioSeiUNoStro").isEmpty){
                println("presente1")
            }
            ticketRepository.save(Ticket().apply {
                id = "CiaoMarioSeiUNoStronzo1"
            })
            ticketRepository.save(Ticket().apply {
                id = "CiaoMarioSeiUNoStronzo"
            })
             ticketRepository.save(Ticket().apply {
            id = "CiaoMarioSeiUNoStronzo3"
        })

        }
    }}
    fun main(args: Array<String>) {
        runApplication<ServerApplication>(*args)
    }


//@SpringBootApplication
//class ServerApplication
//
//fun main(args: Array<String>) {
//    runApplication<ServerApplication>(*args)
//}


