package com.example.classes

import kotlinx.serialization.Serializable

@Serializable
data class RoomState(
    val connectedPlayers: List<String> = emptyList() ,
) {

}