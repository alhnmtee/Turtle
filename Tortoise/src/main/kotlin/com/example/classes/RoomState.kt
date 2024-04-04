package com.example.classes

import kotlinx.serialization.Serializable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.collections.emptyList
import kotlin.emptyArray
import java.util.Dictionary

@Serializable
data class RoomState(
    val connectedPlayers: List<String> = emptyList(),
    val playersCurrentlyPlaying :List<String> = emptyList(),

    val gameRooms: Map<String, Map<Int, List<String>>> = mapOf(
    "normal" to mapOf(
        3 to emptyList(),
        4 to emptyList(),
        5 to emptyList()
    ),
    "random" to mapOf(
        3 to emptyList(),
        4 to emptyList(),
        5 to emptyList()
    ),
),
    val requests : Map<String , String> = emptyMap(),
) {
    
}