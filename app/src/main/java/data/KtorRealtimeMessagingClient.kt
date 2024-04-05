package data

import com.example.classes.RoomState
import com.google.firebase.auth.FirebaseAuth
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.json.Json

//Bağlantı burada yapılıyor , bir flow oluşturuluyor ve AppModule adlı yerde de hilt'e sağlanıyor
class KtorRealtimeMessagingClient(
    private val client: HttpClient,
):RealTimeMessagingClient{
    private var session : WebSocketSession? = null

    override fun getRoomStateStream(mode:String,letterCount:Int): Flow<RoomState> {
        return flow{
            session = client.webSocketSession {
                url("ws://192.168.1.41:8080/room/$mode/$letterCount/${FirebaseAuth.getInstance().currentUser?.uid}")
            }
            val roomStates = session!!
                .incoming
                .consumeAsFlow()
                .filterIsInstance<Frame.Text>()
                .mapNotNull { Json.decodeFromString<RoomState>(it.readText()) }

            emitAll(roomStates)
        }
    }





    override suspend fun close() {
        session?.close()
        session=null
    }
}