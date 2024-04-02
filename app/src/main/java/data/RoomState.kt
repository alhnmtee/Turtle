package com.example.classes

import kotlinx.serialization.Serializable

//websocket da kullanıcılar arası paylaşılan oda bilgilerini tutan class bunu gibi bir de oyun için yazılacak
@Serializable
data class RoomState(
    val connectedPlayers: List<String> = emptyList() ,
) {

}