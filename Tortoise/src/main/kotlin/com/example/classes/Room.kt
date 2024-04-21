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
import java.io.BufferedReader
import java.io.InputStreamReader
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.Frame
import kotlinx.coroutines.flow.*

import io.ktor.websocket.*
import io.ktor.client.request.request
import kotlinx.coroutines.*

import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.serialization.encodeToString
import kotlin.random.Random
import javax.naming.Context

class Room(
    val listOfWords : List<String>
) {
    private val _state = MutableStateFlow(RoomState())
    private val state: StateFlow<RoomState> = _state

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



    suspend fun broadcast(state: RoomState) {
        playerSockets.keys.forEach { key ->
            if (state.playersCurrentlyPlaying.contains(key)) {
                ongoingGames.values.forEach { ongoingGame ->
                    if (ongoingGame.value.connectedPlayers.contains(key)) {
                        println("Oyun oynaya giden oda State: $ongoingGame.value")
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

    suspend fun disconnectPlayer(player: String) {
        playerSockets.remove(player)
        disconnectFromGame(player)
        _state.update {
            it.copy(
                connectedPlayers = it.connectedPlayers - player,
                playersCurrentlyPlaying = it.playersCurrentlyPlaying - player
            )

        }

    }



    suspend fun getWordFromPlayer(uidSender: String, word: String){
        ongoingGames.values.forEach { ongoingGame ->
            if (ongoingGame.value.connectedPlayers.contains(uidSender)) {
                val wordScore = getWordScore(word, if(uidSender == ongoingGame.value.player1Id) ongoingGame.value.player1Word else ongoingGame.value.player2Word)
                val totalScore = wordScore.sum()
                if(uidSender == ongoingGame.value.player1Id){
                    ongoingGame.update {
                        it.copy(
                            player1Game = it.player1Game + (word to wordScore),
                            player1Score = it.player1Score + totalScore
                        )
                    }

                    if(word == ongoingGame.value.player1Word && ongoingGame.value.playerWon==""){
                        //  println("BİR OYUNCU OYUNU KAZANDI                     AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBBBBB")
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
                            player2Game = it.player2Game + (word to wordScore),
                            player2Score = it.player2Score + totalScore
                        )
                    }

                    if(word == ongoingGame.value.player2Word && ongoingGame.value.playerWon== ""){
                        ongoingGame.update {
                            it.copy(
                                playerWon = uidSender,
                                
                            )
                        }
                    }
                }
                broadcast(state.value)
            }
        }
    }
    suspend fun playerWon(uidSender: String){
        ongoingGames.values.forEach { ongoingGame ->
            if (ongoingGame.value.connectedPlayers.contains(uidSender)) {
              if(uidSender == ongoingGame.value.player1Id){
                    ongoingGame.update {
                        it.copy(
                            playerWon = ongoingGame.value.player2Id,
                        )
                    }
                }
                if(uidSender == ongoingGame.value.player2Id){
                    ongoingGame.update {
                        it.copy(
                            playerWon = ongoingGame.value.player1Id,
                        )
                    }
                }
                broadcast(state.value)
            }
        }
    }

    suspend fun disconnectFromGame(uidSender : String){

        ongoingGames.values.forEach { ongoingGame ->
            if (ongoingGame.value.connectedPlayers.contains(uidSender)) {
                ongoingGame.update {
                    it.copy(
                        connectedPlayers = it.connectedPlayers - uidSender,
                        requests = it.requests - uidSender
                    )
                }
                if(ongoingGame.value.connectedPlayers.isEmpty()){
                    try{
                        ongoingGames.remove(ongoingGame.value.player1Id)
                        ongoingGames.remove(ongoingGame.value.player2Id)
                    }
                    catch(e:Exception){
                        ongoingGames.remove(ongoingGame.value.player2Id)
                        println("Ongoing game not found")
                    }
                }
                if(ongoingGame.value.playerWon == "" && ongoingGame.value.player1Id == uidSender){
                    ongoingGame.update {
                        it.copy(
                            playerWon = it.player2Id
                        )
                    }
                }
                else if(ongoingGame.value.playerWon == "" && ongoingGame.value.player2Id == uidSender){
                    ongoingGame.update {
                        it.copy(
                            playerWon = it.player1Id
                        )
                    }
                }
                broadcast(state.value)
            }
            else{

            }

        }
        _state.update {
            it.copy(
                playersCurrentlyPlaying = it.playersCurrentlyPlaying - uidSender,
                requests = it.requests - uidSender,
                rejectedPlayers = it.rejectedPlayers - uidSender,
            )
        }

        state.value.requests.forEach{
            val key = it.key
            val value = it.value
            if(value==uidSender){
                _state.update{
                    it.copy(
                        requests = it.requests - key,
                        rejectedPlayers = it.rejectedPlayers + key
                    )
                }
            }
        }

        broadcast(state.value)
    }

    private suspend fun getWordScore(word:String , answerWord : String) : List<Int> {
        val array = Array(answerWord.length){0}
        for(i in 0..<answerWord.length){
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
                broadcast(state.value)
            }
            else{
                println("Player not found in ongoing games")
            }

        }

    }


    suspend fun denyGameRequest(uidSender: String, uidReciever: String){
        ongoingGames.values.forEach { ongoingGame ->
            if (ongoingGame.value.connectedPlayers.contains(uidSender)) {
                ongoingGame.update {
                    it.copy(
                        requests = it.requests - ("$uidReciever"),
                        rejectedPlayers = it.rejectedPlayers + uidReciever
                    )
                }
                broadcast(state.value)
                return
            }
            else{

            }

        }
        _state.update {
            it.copy(
                requests = it.requests - ("$uidReciever"),
                rejectedPlayers = it.rejectedPlayers + uidReciever
            )
        }

    }

    suspend fun confirmDenial(uidSender :String){
        ongoingGames.values.forEach { ongoingGame ->
            if (ongoingGame.value.connectedPlayers.contains(uidSender)) {
                ongoingGame.update {
                    it.copy(
                        rejectedPlayers = it.rejectedPlayers - uidSender
                    )
                }
                broadcast(state.value)
                return
            }
            else{

            }

        }
        _state.update{
            it.copy(
                rejectedPlayers = it.rejectedPlayers - uidSender
            )
        }

    }

    suspend fun sendGameRequest(uidSender: String, uidReciever: String) {
        ongoingGames.values.forEach { ongoingGame ->
            if (ongoingGame.value.connectedPlayers.contains(uidSender)) {
                ongoingGame.update {
                    it.copy(
                        requests = it.requests + ("$uidSender" to "$uidReciever")
                    )
                }
                broadcast(state.value)
                return
            }
            else{

            }

        }

        //al json = Json.encodeToString(state)
        _state.update {
            it.copy(
                requests = it.requests + ("$uidSender" to "$uidReciever")
            )
        }
        broadcast(state.value)

    }

    suspend fun confirmGameRequest(uidSender: String, uidReciever: String , mode :String , letterCount :Int) {
        ongoingGames.values.forEach { ongoingGame ->
            if (ongoingGame.value.connectedPlayers.contains(uidSender)) {
                ongoingGame.update {
                    it.copy(
                        requests = it.requests - ("$uidReciever"),
                        //playersCurrentlyPlaying = it.playersCurrentlyPlaying + uidSender,
                    )
                }
                startGame(uidSender, uidReciever , mode , letterCount)
                //broadcast(ongoingGame.value)
                return
            }
            else{

            }

        }

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
        println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA : $state")
        startGame(uidSender, uidReciever , mode , letterCount)

    }

    private suspend fun startGame(uidSender: String, uidReciever: String,mode:String,letterCount : Int) {
        var word :String= ""
        if(mode == "random"){
            val newList = listOfWords.filter{it.length == letterCount}

            val randomIndex = Random.nextInt(0, newList.size)
            word = newList.get(randomIndex)

        }

        ongoingGames.values.forEach { ongoingGame ->
            if (ongoingGame.value.connectedPlayers.contains(uidSender)) {
                ongoingGame.update {
                    it.copy(
                        player1Word=word,
                        player2Word=word,
                        playerWon = "",
                        player1Game=emptyMap(),
                        player2Game=emptyMap(),
                    )

                }
                println("Game started with word $word")
                if(mode=="random"){
                    val randomCharIndex = Random.nextInt(0, word.length)
                    ongoingGame.update {
                        it.copy(
                            randomCharIndex = randomCharIndex
                        )
                    }
                }
                broadcast(state.value)
                return
            }
            else{

            }

        }


        val newRoomState = MutableStateFlow(RoomState())

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
        if(mode=="random"){
            val randomCharIndex = Random.nextInt(0, word.length)
            newRoomState.update {
                it.copy(
                    randomCharIndex = randomCharIndex
                )
        }
        broadcast(state.value)
    }
}
}
