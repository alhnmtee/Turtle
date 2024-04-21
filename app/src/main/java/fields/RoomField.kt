package fields

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.classes.RoomState

//TODO wordle oynanış kısmı burada olucak
@Composable
fun RoomField (
    state: RoomState,
    onPlayerClicked : (String) -> Unit
){
    Column() {
        // Display connected players
        Text("Connected Players:", modifier = Modifier.padding(8.dp))
        Column() {
            state.connectedPlayers.forEach { playerName ->
                val playerStatus = if (state.playersCurrentlyPlaying.contains(playerName)) "(Oyunda)" else "(Lobide)"
                Text(text = "$playerName $playerStatus" , color = Color.White , modifier = Modifier.clickable {
                    onPlayerClicked(playerName)
                })
            }
        }
    }
}
