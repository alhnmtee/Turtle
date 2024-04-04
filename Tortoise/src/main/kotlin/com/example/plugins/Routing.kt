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
    val rooms = Room()
    
    routing{
        
        socketRoom(rooms)
       
    }


}

