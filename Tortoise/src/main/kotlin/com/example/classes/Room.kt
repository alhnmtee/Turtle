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
class Room(
){
    private val state = MutableStateFlow(RoomState())

    private val playerSockets = ConcurrentHashMap<String , WebSocketSession>()

    private val roomScope  = CoroutineScope(SupervisorJob()+Dispatchers.IO )

    init{
        roomScope.launch {
            // Emit a value every 10 seconds
            flow {
                while (true) {
                    emit(Unit) // Emit a value
                    delay(10000) // Delay for 10 seconds
                }
            }.onEach { broadcast(state.value) } // Call broadcast with the current state every 10 seconds
            .launchIn(roomScope)
        }
    }

    init{
        state.onEach(::broadcast).launchIn(roomScope)
        
       
    }

    fun connectPlayer(session:WebSocketSession , uId : String):String?{
        val player = uId
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

    suspend fun broadcast(state: RoomState) {
        val json = Json.encodeToString(state)
        val frame = Frame.Text(json)

        playerSockets.values.forEach { socket ->
            socket.send(frame)
        }
    }

    suspend fun sendGameRequest(uidSender : String,uidReciever:String){
        //al json = Json.encodeToString(state)
        state.update{
            it.copy(
                requests = it.requests + ("$uidSender" to "$uidReciever")
            )
        }
    }

    suspend fun confirmGameRequest(uidSender : String,uidReciever:String){
            
            state.update{

                it.copy(
                    requests = it.requests - ("$uidReciever")
                )

                it.copy(
                    playersCurrentlyPlaying = it.playersCurrentlyPlaying + uidSender
                )
                it.copy(

                    playersCurrentlyPlaying = it.playersCurrentlyPlaying + uidReciever
                )

            }

            
    }

    

}   