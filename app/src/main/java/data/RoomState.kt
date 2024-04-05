package com.example.classes

import kotlinx.serialization.Serializable

@Serializable
data class RoomState(
    val connectedPlayers: List<String> = emptyList(),
    val playersCurrentlyPlaying :List<String> = emptyList(),
    val requests : Map<String , String> = emptyMap(),
) {

}