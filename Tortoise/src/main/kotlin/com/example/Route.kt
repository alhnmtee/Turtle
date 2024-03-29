package com.example.routes

import com.example.classes.User
import com.example.classes.FIREBASE_AUTH
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authenticatedRoute() {
    authenticate(FIREBASE_AUTH) {
        get("/authenticated") {
            val user: User =
                call.principal() ?: return@get call.respond(HttpStatusCode.Unauthorized)
            call.respond("User is authenticated: $user")
        }
    }
}