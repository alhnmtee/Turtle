import io.ktor.server.websocket.*
import io.ktor.server.routing.*
import io.ktor.websocket.CloseReason
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json
import com.example.main
import io.ktor.server.routing.*
import WordleGame
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
                        if(type== "recieve_game_request"){
                            room.confirmGameRequest(player, body)
                        } 
                        //odalar için routing işlemi falan fişman yapılacak
                    }
                }
            }
            catch( e : Exception){
                e.printStackTrace()
            }
            finally{
                room.disconnectPlayer(player)
            }

        }
    }
}
