package data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classes.GameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.ConnectException
import javax.inject.Inject

//TODO bu düzeltilecek


@HiltViewModel
class WordleViewModel @Inject constructor(
    private val client: RealTimeMessagingClient
):ViewModel(){
    val state = client
        .getGameStateStream()
        .onStart { _isConnecting.value = true }
        .onEach { _isConnecting.value=false }
        .catch { t : Exception -> _showConnectionError.value = t is ConnectException }
        .stateIn(
            scope = viewModelScope ,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = GameState()
        )

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