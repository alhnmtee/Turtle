package com.example

import Room
import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true);
}

fun Application.module() {
    val room1 = Room()
    val room2 = Room()
    val room3 = Room()

    val room4 = Room()
    val room5 = Room()
    val room6 = Room()
    
    configureSecurity()
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureSockets()
    configureRouting(room1,room2,room3,room4,room5,room6)
}
