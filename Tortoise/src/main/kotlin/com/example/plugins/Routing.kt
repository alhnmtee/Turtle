package com.example.plugins

import WordleGame
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
    val room = Room(letterCount = 6,gameMode = "normal")
    val room2 = Room(letterCount = 5,gameMode = "normal")
    val room3= Room(letterCount = 4,gameMode = "normal")
    
    val room4 = Room(letterCount = 6,gameMode = "random")
    val room5 = Room(letterCount = 5,gameMode = "random")
    val room6= Room(letterCount = 4,gameMode = "random")
    routing{
        
        socketRoom(room)
        socketRoom(room2)
        socketRoom(room3)
        
        socketRoom(room4)
        socketRoom(room5)
        socketRoom(room6)
    }


}

