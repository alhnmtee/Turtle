package com.example.classes

import kotlinx.serialization.Serializable

@Serializable
data class RoomState(
    val connectedPlayers: List<String> = emptyList(),
    val playersCurrentlyPlaying :List<String> = emptyList(),
    val requests : Map<String , String> = emptyMap(),


    val isGamePlaying :Boolean = false,
    val player1Word:String = "",
    val player2Word:String = "",
    val player1Game : List<String> = emptyList(),
    val player2Game : List<String> = emptyList(),
    val player1Score : Int = 0,
    val player2Score : Int = 0,


    ) {

}