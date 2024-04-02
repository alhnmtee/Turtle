import io.ktor.server.websocket.*
import io.ktor.server.routing.*
import io.ktor.websocket.CloseReason
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import com.example.main

fun Route.socket(game : WordleGame){
    route("/play/${game.gameId}"){
        webSocket{
            val player = game.connectPlayer(this)
        

            if(player == null){
                close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT , " Zaten 2 Oyuncu var bu oyunda"))
                return@webSocket
            }

            try{
                incoming.consumeEach{ frame ->
                    if(frame is Frame.Text){
                        val action = frame.readText()
                        //burada gelen veriye göre oyunu bitirme filan fişman şeyler yapılacak
                    }
                }
            }
            catch( e : Exception){

            }
            finally{
                game.disconnectPlayer(player)
            }

        }
    }
   
}

fun Route.socketRoom(room : Room){
    route("/room/${room.gameMode}/${room.letterCount}/{userId}"){
        webSocket{
            val uId = call.parameters["userId"]
            val player = room.connectPlayer(this,uId.toString())
            if(player == null)
            {
                close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT , "Bağlanılamadı"))
                return@webSocket
            }
            try{
                incoming.consumeEach{ frame ->
                    if(frame is Frame.Text){
                        val action = frame.readText()
                        //odalar için routing işlemi falan fişman yapılacak
                    }
                }
            }
            catch( e : Exception){
            
            }
            finally{
                room.disconnectPlayer(player)
            }

        }
    }
}