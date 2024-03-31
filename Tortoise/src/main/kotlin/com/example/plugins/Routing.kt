package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

import socketRoom
import Room
import com.example.plugins.configureHTTP

fun Application.configureRouting() {
    routing{
        //5 lik oda aç sonra 4 sonra 6 harflik , istenilen kişinin katılıp birbirini görebileceği odalar
        val room = Room(letterCount = 6,gameMode = "Fixed")
        socketRoom(room)

        val room2 = Room(letterCount = 5,gameMode = "Fixed")
        socketRoom(room2)

        val room3= Room(letterCount = 4,gameMode = "Fixed")
        socketRoom(room3)


        
        val room4 = Room(letterCount = 6,gameMode = "Random")
        socketRoom(room4)

        val room5 = Room(letterCount = 5,gameMode = "Random")
        socketRoom(room5)

        val room6= Room(letterCount = 4,gameMode = "Random")
        socketRoom(room6)
    }
    
   
}

