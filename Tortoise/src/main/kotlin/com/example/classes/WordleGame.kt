import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.*
import com.example.classes.GameState
import io.ktor.websocket.*
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.flow.*

class WordleGame(
    val letterCount : Int
){
    private val state = MutableStateFlow(GameState(letterCount = letterCount))

    private val playerSockets = ConcurrentHashMap<Char , WebSocketSession>()

    private val gameScope  = CoroutineScope(SupervisorJob()+Dispatchers.IO )

    init{
        state.onEach(::broadcast).launchIn(gameScope)
    }

    fun connectPlayer(session:WebSocketSession):Char?{
        val isPlayer1 = state.value.connectedPlayers.any{ it  == '1'}
        val player = if(isPlayer1) '2' else '1'

        state.update{
            if(state.value.connectedPlayers.contains(player)){
                return null
            }
            if(!playerSockets.containsKey(player)){
                playerSockets[player] = session
            }

            it.copy(
                connectedPlayers = it.connectedPlayers + player
            )

            
        }

        return player
    }

    fun disconnectPlayer(player: Char) {
        playerSockets.remove(player)
        state.update { it.copy(connectedPlayers = it.connectedPlayers - player) }

        gameScope.launch {
            playerSockets[player]?.close()
        }
    }

    suspend fun broadcast(state: GameState) {
        val json = Json.encodeToString(state)
        val frame = Frame.Text(json)
        
        playerSockets.values.forEach { socket ->
            socket.send(frame)
        }
    }

    
}