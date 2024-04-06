package data

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classes.RoomState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    public override fun onCleared() {
        super.onCleared()
        job?.cancel() // Cancel the job when ViewModel is cleared
        viewModelScope.launch {
            client.close()
        }
    }
}
