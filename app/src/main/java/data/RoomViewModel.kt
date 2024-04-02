package data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classes.RoomState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.ConnectException
import javax.inject.Inject

//TODO bu düzeltilecek


//hiltciewmodel lazım ki neyi göstereceğini bilsin
@HiltViewModel
class RoomViewModel @Inject constructor(
    private val client: RealTimeMessagingClient,
    ):ViewModel(){
    private var mode : String = ""
    private var letterCount : Int= 0

    fun setVars(mode : String,letterCount :Int)
    {
        this.mode = mode
        this.letterCount = letterCount
    }

    //statee i oluşturuyoruz , bunu roomstate taban alınarak KtorRealtimeMessagingClient dan oluşturuyoruz yani bağlantı orda yapılıyor sonra buradan state i çekiyoruz
    var state : StateFlow<RoomState> = client
        .getRoomStateStream(mode,letterCount)
        .onStart { _isConnecting.value = true }
        .onEach { _isConnecting.value=false }
        .catch { t -> _showConnectionError.value = t is ConnectException }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RoomState())

    private val _isConnecting = MutableStateFlow(false)
    val isConnecting = _isConnecting.asStateFlow()

    private val _showConnectionError = MutableStateFlow(false)
    val showConnectionError = _showConnectionError.asStateFlow()


    fun makeState() {
        this.state = client
            .getRoomStateStream(mode,letterCount)
            .onStart { _isConnecting.value = true }
            .onEach { _isConnecting.value=false }
            .catch { t -> _showConnectionError.value = t is ConnectException }
            .stateIn(
                scope = viewModelScope ,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = RoomState()
            )
    }





    @Override
    override fun onCleared(){
        super.onCleared()
        viewModelScope.launch {
            client.close()
        }
    }

}
