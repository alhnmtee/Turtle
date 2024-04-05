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
    val requests : Map<String , String> = emptyMap(),
) {
     
}