import io.ktor.server.websocket.*
import io.ktor.server.routing.*
import io.ktor.websocket.CloseReason
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach

fun Route.socket(game : WordleGame){
    route("/play"){
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
                        //odalar için routing işlemi falan fişman yapılacak
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