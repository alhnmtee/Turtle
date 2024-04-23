package com.example

import Room
import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.File
import javax.naming.Context

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true);
}

fun Application.module() {

    val wordsList = readWordsFromFile()

    val room1 = Room(wordsList)
    val room2 = Room(wordsList)
    val room3 = Room(wordsList)
    val room4 = Room(wordsList)

    val room5 = Room(wordsList)
    val room6 = Room(wordsList)
    val room7 = Room(wordsList)
    val room8 = Room(wordsList)

    val room9 = Room(wordsList)
    val room10 = Room(wordsList)
    val room11 = Room(wordsList)
    val room12 = Room(wordsList)
    
    configureSecurity()
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureSockets()
    configureRouting(room1,room2,room3,room4,room5,room6,room7,room8,room9,room10,room11,room12)
}


private fun readWordsFromFile(): List<String> {
    val wordsList = mutableListOf<String>()
    File("src\\main\\kotlin\\com\\example\\kelimeler.txt").useLines() { wordsList.addAll(it) }
    return wordsList
}

