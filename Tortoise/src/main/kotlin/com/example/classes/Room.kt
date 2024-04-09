import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.*
import kotlinx.serialization.serializer
import kotlinx.serialization.serializer
import kotlinx.serialization.serializer
import com.example.classes.RoomState
import java.util.concurrent.ConcurrentHashMap
import java.io.File
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.Frame
import kotlinx.coroutines.flow.*

import io.ktor.websocket.*
import kotlinx.coroutines.*

import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.serialization.encodeToString
import kotlin.random.Random

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
        playerSockets.keys.forEach { key ->
            if (state.playersCurrentlyPlaying.contains(key)) {
                ongoingGames.values.forEach { ongoingGame ->
                    if (ongoingGame.value.connectedPlayers.contains(key)) {
                        println("Oyun oynaya giden oda State: $ongoingGame")
                        playerSockets[key]!!.send(
                            Json.encodeToString(RoomState.serializer(),ongoingGame.value)
                        )
                    }
                    
                }
            } else {
                println("Normal oda State: $state")
                playerSockets[key]!!.send(
                    Json.encodeToString(RoomState.serializer(), state)
                )
            }
        }
    }
    //#TODO BU skorlama için girilen kelimeyi alıp skorlayan fonksiyon di mi?????
    suspend fun getWordFromPlayer(uidSender: String, word: String){
        ongoingGames.values.forEach { ongoingGame ->
            if (ongoingGame.value.connectedPlayers.contains(uidSender)) {
                if(uidSender == ongoingGame.value.player1Id){
                    ongoingGame.update {
                        it.copy(
                           player1Game = it.player1Game + (word to getWordScore(word,it.player1Word)),
                        )
                    }

                    if(word == ongoingGame.value.player1Word && ongoingGame.value.playerWon==""){
                        ongoingGame.update {
                            it.copy(
                               playerWon = uidSender,
                            )
                        }
                    }

                }


                else if (uidSender == ongoingGame.value.player2Id){
                    ongoingGame.update {
                        it.copy(
                           player2Game = it.player2Game + (word to getWordScore(word,it.player2Word)),
                        )
                    }

                    if(word == ongoingGame.value.player2Word && ongoingGame.value.playerWon==""){
                        ongoingGame.update {
                            it.copy(
                               playerWon = uidSender,
                            )
                        }
                    }
                }
                
            }
            else{
                
            }
            
        }
    }

    suspend fun disconnectFromGame(uidSender : String){
        ongoingGames.values.forEach { ongoingGame ->
            if (ongoingGame.value.connectedPlayers.contains(uidSender)) {
                ongoingGame.update { 
                  it.copy(
                    connectedPlayers = it.connectedPlayers - uidSender
                  )  
                }
                
            }
            else{
                
            }
            
        }
    }

    private suspend fun getWordScore(word:String , answerWord : String) : List<Int> {
        val array = Array(answerWord.length){0}
        for(i in 0..answerWord.length){
            if(word.get(i) == answerWord.get(i)){
                array[i]=10
            }
            else if(answerWord.contains(word.get(i))){
                array[i]=5
            }
        }
        return array.toList()
    }

    suspend fun setOtherPlayerWord(uidSender: String, word: String){
        println("setOtherPlayerWord : Attempting to set word: $word for user: $uidSender")
        ongoingGames.values.forEach { ongoingGame ->
            if (ongoingGame.value.connectedPlayers.contains(uidSender)) {
                if(uidSender == ongoingGame.value.player1Id){
                    ongoingGame.update {
                        it.copy(
                            player2Word = word

                        )
                    }
                    println("player2Word set to $word")
                }
                else if (uidSender == ongoingGame.value.player2Id){
                    ongoingGame.update {
                        it.copy(
                            player1Word = word

                        )

                    }
                    println("player1Word set to $word")
                }
                broadcast(ongoingGame.value)
            }
            else{
                println("Player not found in ongoing games")
            }

        }

    }


    suspend fun denyGameRequest(uidSender: String, uidReciever: String){
        _state.update {
            it.copy(
                requests = it.requests - ("$uidReciever"),
                rejectedPlayers = it.rejectedPlayers + uidReciever
            )
        }
    }

    suspend fun confirmDenial(uidSender :String){
        _state.update{
            it.copy(
                rejectedPlayers = it.rejectedPlayers - uidSender
            )
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

    suspend fun confirmGameRequest(uidSender: String, uidReciever: String , mode :String , letterCount :Int) {
        _state.update {
            it.copy(
                requests = it.requests - ("$uidReciever"),
                playersCurrentlyPlaying = it.playersCurrentlyPlaying + uidSender,
            )
        }
        _state.update {
            it.copy(
                playersCurrentlyPlaying = it.playersCurrentlyPlaying + uidReciever 
            )
        }
        startGame(uidSender, uidReciever , mode , letterCount)
    }

    private fun startGame(uidSender: String, uidReciever: String,mode:String,letterCount : Int) {
        if (ongoingGames.containsKey(uidReciever)) {
            return
        }

        val newRoomState = MutableStateFlow(RoomState())
        var word :String= ""
        if(mode == "random"){
            //val wordsList = File("..../resources/kelimeler.txt").useLines{ lines -> lines.filter {it.length == letterCount}.toList() }
            //val randomIndex = Random.nextInt(0, wordsList.size)
            //word = wordsList.get(randomIndex)
            word = ""
        }
        newRoomState.update {
            it.copy(
                isGamePlaying = true,
                connectedPlayers = it.connectedPlayers + uidSender + uidReciever,
                player1Id = uidReciever,
                player2Id = uidSender,
                player1Word=word,
                player2Word=word,
            )
        }

        ongoingGames.put(uidReciever, newRoomState)
    }
}