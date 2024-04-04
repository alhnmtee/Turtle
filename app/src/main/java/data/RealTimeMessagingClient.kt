package data

import com.example.classes.RoomState
import kotlinx.coroutines.flow.Flow

//KtorRealtimeMessagingClient için interface
interface RealTimeMessagingClient {
    fun getRoomStateStream() : Flow<RoomState>
    //TODO buraya oyunu bitrme ve kelime seçmenin suspend şeklinde class ve fonskiyonlaru da gelicek ama bunları serverda da yazmk gerekitor
    suspend fun close()
}