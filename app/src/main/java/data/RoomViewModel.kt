package data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classes.RoomState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
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
    ):ViewModel(){


    val state : StateFlow<RoomState> = client
            .getRoomStateStream(mode,letterCount)
            .onStart { _isConnecting.value = true }
            .onEach { _isConnecting.value=false }
            .catch { t -> _showConnectionError.value = t is ConnectException }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RoomState())




    private val _isConnecting = MutableStateFlow(false)
    val isConnecting = _isConnecting.asStateFlow()

    private val _showConnectionError = MutableStateFlow(false)
    val showConnectionError = _showConnectionError.asStateFlow()

    @Override
    override fun onCleared(){
        super.onCleared()
        viewModelScope.launch {
            client.close()
        }
    }

}
