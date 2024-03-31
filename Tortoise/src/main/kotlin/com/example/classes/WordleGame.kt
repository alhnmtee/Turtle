import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.*
import com.example.classes.GameState
import java.util.concurrent.ConcurrentHashMap
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.Frame
import kotlinx.coroutines.flow.*

class WordleGame(
    val gameMode : String,
    val letterCount : Int,
    val gameId : Int,
){
    private val state = MutableStateFlow(GameState())

    private val playerSockets = ConcurrentHashMap<String , WebSocketSession>()

    private val gameScope  = CoroutineScope(SupervisorJob()+Dispatchers.IO )
    
    //oyun başu gameId oluşturup oyunculara gönderilecek ve buna göre bağlanıcak

    init{
        state.onEach(::broadcast).launchIn(gameScope)
    }

    fun connectPlayer(session:WebSocketSession):String?{
        val isPlayer1 = state.value.connectedPlayers.any{ it  == "1"}
        val player = if(isPlayer1) "2" else "1"

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

    fun disconnectPlayer(player : String){
        playerSockets.remove(player)
        state.update {
            it.copy(
                connectedPlayers = it.connectedPlayers - player
            )
         }
    }

    suspend fun broadcast(state: GameState) {
        val json = Json.encodeToString(state)
        val frame = Frame.Text(json)
        
        playerSockets.values.forEach { socket ->
            socket.send(frame)
        }
    }

    //TODO oyun mantığı buraya state'ı düzenleyerek ve kontrol ederek yapılacak

    
}