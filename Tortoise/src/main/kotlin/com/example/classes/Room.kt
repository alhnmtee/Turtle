import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.*
import com.example.classes.GameState
import com.example.classes.RoomState
import java.util.concurrent.ConcurrentHashMap
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.Frame
import kotlinx.coroutines.flow.*
var playerCount :Int = 1
class Room(
    val gameMode : String,
    val letterCount:Int
){
    private val state = MutableStateFlow(RoomState())

    private val playerSockets = ConcurrentHashMap<String , WebSocketSession>()

    private val roomScope  = CoroutineScope(SupervisorJob()+Dispatchers.IO )

    init{
        state.onEach(::broadcast).launchIn(roomScope)
    }

    fun connectPlayer(session:WebSocketSession):String{
        val player : String = playerCount.toString()
        playerCount= playerCount+ 1

        state.update{
            if(!playerSockets.containsKey(player)){
                playerSockets[player] = session
            }

            it.copy(
                connectedPlayers = it.connectedPlayers + player
            )
        }

        return player
    }

    fun disconnectPlayer(player : String){
        playerSockets.remove(player)
        state.update {
            it.copy(
                connectedPlayers = it.connectedPlayers - player
            )
         }
    }

    suspend fun broadcast(state: RoomState) {
        val json = Json.encodeToString(state)
        val frame = Frame.Text(json)

        playerSockets.values.forEach { socket ->
            socket.send(frame)
        }
    }

}   