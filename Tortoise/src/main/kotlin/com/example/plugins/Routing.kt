package com.example.plugins

import WordleGame
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import socket

fun Application.configureRouting() {
    val game = WordleGame(letterCount = 5) // letterCount değerini ihtiyacınıza göre ayarlayın
    routing {
        socket(game)
    }


        
        // Static plugin. Try to access `/static/index.html`
        //static("/static") {
        //    resources("static")
        //}
    }

