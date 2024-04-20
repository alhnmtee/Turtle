import io.ktor.server.websocket.*
import io.ktor.server.routing.*
import io.ktor.websocket.CloseReason
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json
import com.example.main
import io.ktor.server.routing.*
import kotlinx.coroutines.launch
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun Route.socketRoom(room : Room,mode:String,letterCount:Int){
    route("/room/$mode/$letterCount/{userId}"){
        webSocket{
            val uId = call.parameters["userId"]
            val player = room.connectPlayer(this,uId.toString())
            if(player == null)
            {
                close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT , "Bağlanılamadı , kullanıcnı zaten var"))
                return@webSocket
            }
            try{
                incoming.consumeEach{ frame ->
                    if(frame is Frame.Text){
                        val action = frame.readText()

                        val type = action.substringBefore("#")
                        val body = action.substringAfter("#")

                        if(type == "send_game_request"){
                            room.sendGameRequest(player, body)
                        }
                        if(type == "deny_game_request"){
                            room.denyGameRequest(player, body)
                        }
                        if(type=="set_player_word"){
                            room.setOtherPlayerWord(player,body)
                        }
                        if(type == "get_word_from_player"){
                            room.getWordFromPlayer(player,body)
                        }
                        if(type == "got_denied"){
                            room.confirmDenial(player)
                        }
                        if(type== "confirm_game_request"){
                            room.confirmGameRequest(player, body.substringBefore("*"),body.substringAfter("*").substringBefore("-"),body.substringAfter("*").substringAfter("-").toInt())
                        }
                        if(type =="disconnect_from_server"){
                            room.disconnectPlayer(player)
                        }
                        if(type =="disconnect_from_game"){
                            room.disconnectFromGame(player)
                        }
                        if(type=="player_won"){
                            room.playerWon(player)
                        }

                        //odalar için routing işlemi falan fişman yapılacak
                    }
                }

            }
            catch( e : Exception){
                e.printStackTrace()
            }
            finally{
                room.disconnectFromGame(player)
                room.disconnectPlayer(player)
            }


        }
    }
}
