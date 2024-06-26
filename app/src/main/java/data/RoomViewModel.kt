package data

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classes.RoomState
import com.google.firebase.auth.FirebaseAuth
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

//TODO bu düzeltilecek


//hiltciewmodel lazım ki neyi göstereceğini bilsin
@AssistedFactory
interface RoomViewModelFactory{
    fun create(mode:String, letterCount: Int) : RoomViewModel
}
@HiltViewModel(assistedFactory = RoomViewModelFactory::class)
class RoomViewModel @AssistedInject constructor(
    private val client: RealTimeMessagingClient,
    @Assisted val mode: String,
    @Assisted val letterCount: Int
) : ViewModel() {

    private val _isConnecting = MutableStateFlow(false)
    val isConnecting: StateFlow<Boolean> = _isConnecting.asStateFlow()

    private val _showConnectionError = MutableStateFlow(false)
    val showConnectionError: StateFlow<Boolean> = _showConnectionError.asStateFlow()

    private val _roomState = MutableStateFlow(RoomState())
    val state: StateFlow<RoomState> = _roomState

    private var job: Job? = null

    init {
        connect()
    }

    private fun connect() {
        job?.cancel() // Cancel existing job if any
        job = viewModelScope.launch {
            _isConnecting.value = true
            try {
                client.getRoomStateStream(mode, letterCount)
                    .collect { roomState ->
                        _roomState.value = roomState
                        _isConnecting.value=false
                    }

            } catch (e: Exception) {
                _showConnectionError.value = true
                Log.e(TAG, "connect: ",e )
                Log.e(TAG, "connect: "+_roomState ,)
                Log.e(TAG, "connect: "+_roomState.value ,)
            } finally {
                _isConnecting.value = false
            }
        }
    }

    fun startGame(senderId: String, receiverId: String) {
        Log.e(TAG, "startGame: : sender =  $senderId : reciever = $receiverId : mode = $mode , letterCount = $letterCount", )
        sendMsg("confirm_game_request#$receiverId*$mode-$letterCount")
    }
    fun denyGame(senderId: String, receiverId: String) {
        sendMsg("deny_game_request#$receiverId")
    }

    fun sendGameRequest(receiverId: String){
        viewModelScope.launch {
            client.sendServerMessage("send_game_request#$receiverId")
            Log.e(TAG, "sendGameRequest: $receiverId", )
            //tüm statei logla
            Log.e(TAG, "sendGameRequest: "+_roomState.value, )
        }
    }
    fun playerWon(receiverId: String) {
        sendMsg("player_won#$receiverId")
        Log.e(TAG, state.value.toString())
    }
    fun sendChar(char: Char,index:Int) {
        sendMsg("send_char#$char#$index")
    }

    fun gotDenied() {
        sendMsg("got_denied#")
    }

    fun sendWord(word:String ,wordsList : List<String>): Boolean {
        return if(wordsList.contains(word)){
            sendMsg("get_word_from_player#$word")
            true
        } else{
            false
        }

    }
    fun setWordForOtherPlayer(word:String,wordsList : List<String>): Boolean {
        Log.d("setWordForOtherPlayer", "Attempting to send word: $word")
        return if(wordsList.contains(word)){
            sendMsg("set_player_word#$word")
            true
        } else{
            false
        }
    }
    fun disconnectFromGame(receiverId: String){
        sendMsg("disconnect_from_game#$receiverId")
        }

    fun disconnectFromWebsocket(receiverId: String){
        sendMsg("disconnect_from_server#$receiverId")
    }


    private fun sendMsg(msg:String){
        viewModelScope.launch{
            client.sendServerMessage(msg)
        }

    }

    public override fun onCleared() {
        super.onCleared()
        job?.cancel() // Cas
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        viewModelScope.launch {
            disconnectFromGame(uid)
            disconnectFromWebsocket(uid)
            client.close()
        }
    }
}