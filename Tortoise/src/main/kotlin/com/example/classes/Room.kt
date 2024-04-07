import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.*
import kotlinx.serialization.serializer
import kotlinx.serialization.serializer
import kotlinx.serialization.serializer
import com.example.classes.GameState
import com.example.classes.RoomState
import java.util.concurrent.ConcurrentHashMap
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.Frame
import kotlinx.coroutines.flow.*

import io.ktor.websocket.*
import kotlinx.coroutines.*

import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.serialization.encodeToString

@Serializable
data class GameState(
    val isGamePlaying: Boolean = false,
    val connectedPlayers: Set<String> = emptySet()
)
class Room(
) {
    private val _state = MutableStateFlow(RoomState())
    val state: StateFlow<RoomState> = _state

    private val playerSockets = ConcurrentHashMap<String, WebSocketSession>()

    private val roomScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val ongoingGames = ConcurrentHashMap<String, MutableStateFlow<RoomState>>()

    init {
        state.onEach(::broadcast).launchIn(roomScope)
    }

    suspend fun connectPlayer(session: WebSocketSession, uId: String): String? {
        val player = uId
        _state.update {
            if (state.value.connectedPlayers.contains(player)) {
                return null
            }
            if (!playerSockets.containsKey(player)) {
                playerSockets[player] = session
            }
            it.copy(connectedPlayers = it.connectedPlayers + player)
        }
        return player
    }

    fun disconnectPlayer(player: String) {
        playerSockets.remove(player)
        _state.update {
            it.copy(connectedPlayers = it.connectedPlayers - player)
        }

    }

    suspend fun broadcast(state: RoomState) {
        val jsonState = Json.encodeToString(RoomState.serializer(), state)
        playerSockets.values.forEach { socket ->
            socket.send(Frame.Text(jsonState))
        }
        playerSockets.keys.forEach { key ->
            if (state.playersCurrentlyPlaying.contains(key)) {
                ongoingGames.values.forEach { ongoingGame ->
                    if (ongoingGame.value.connectedPlayers.contains(key)) {
                        val gameData = ongoingGame.value // Extract relevant game data
                        playerSockets[key]!!.send(
                            Json.encodeToString(RoomState.serializer(), gameData)
                        )
                    }
                }
            } else {
                playerSockets[key]!!.send(
                    jsonState
                )
            }
        }
    }


    suspend fun sendGameRequest(uidSender: String, uidReciever: String) {
        //al json = Json.encodeToString(state)
        _state.update {
            it.copy(
                requests = it.requests + ("$uidSender" to "$uidReciever")
            )
        }
    }

    suspend fun confirmGameRequest(uidSender: String, uidReciever: String) {
        _state.update {
            it.copy(
                requests = it.requests - ("$uidReciever"),
                playersCurrentlyPlaying = it.playersCurrentlyPlaying + uidReciever + uidSender
            )
        }
        startGame(uidSender, uidReciever)
    }

    fun startGame(uidSender: String, uidReciever: String) {
        if (ongoingGames.containsKey(uidReciever)) {
            return
        }

        val roomState = MutableStateFlow(RoomState())
        roomState.update {
            it.copy(
                isGamePlaying = true,
                connectedPlayers = it.connectedPlayers + uidSender + uidReciever
            )
        }

        ongoingGames.put(uidReciever, roomState)
    }
}   