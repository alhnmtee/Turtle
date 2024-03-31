package com.example.classes

import kotlinx.serialization.Serializable

@Serializable
data class GameState(
    val connectedPlayers: List<String> = emptyList() ,
    val winningPlayer : String = "",
    val player1Word:String = "",
    val player2Word:String = "",
    val player1Game : List<String> = emptyList(),
    val player2Game : List<String> = emptyList(),

    ) {

}