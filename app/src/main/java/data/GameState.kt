package data

import kotlinx.serialization.Serializable

@Serializable
data class GameState(
    val connectedPlayers: List<String> = emptyList() ,
    val letterCount : Int
) {

}