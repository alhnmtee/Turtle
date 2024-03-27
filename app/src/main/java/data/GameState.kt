package data

import kotlinx.serialization.Serializable

@Serializable
data class GameState(
    val connectedPlayers: List<Char> = emptyList() ,
    val letterCount : Int
) {

}