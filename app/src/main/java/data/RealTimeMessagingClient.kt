package data

import kotlinx.coroutines.flow.Flow

interface RealTimeMessagingClient {
    fun getGameStateStream() : Flow<GameState>
    //TODO buraya oyunu bitrme ve kelime seçmenin suspend şeklinde class ve fonskiyonlaru da gelicek ama bunları serverda da yazmk gerekitor
    suspend fun close()
}