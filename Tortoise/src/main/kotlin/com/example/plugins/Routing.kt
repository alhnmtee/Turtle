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

fun Application.configureRouting(room1 :Room,room2 :Room,room3 :Room,room4 :Room,room5 :Room,room6 :Room,room7 :Room,room8 :Room) {

    
    routing{
        
        socketRoom(room1,"normal",4)
        socketRoom(room2,"normal",5)
        socketRoom(room3,"normal",6)
        socketRoom(room4,"random",7)

        socketRoom(room5,"random",4)
        socketRoom(room6,"random",5)
        socketRoom(room7,"random",6)
        socketRoom(room8,"random",7)
       
    }


}

