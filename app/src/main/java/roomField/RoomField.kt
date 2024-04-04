package roomField

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
    // Odadaki oyuncların gösterilip , tıklama işlemi filan yaplıacağı yer burası
    Column() {
        // Display connected players
        Text("Connected Players:", modifier = Modifier.padding(8.dp))
        Column() {
            state.connectedPlayers.forEach { playerName ->
                Text(text = playerName , color = Color.White , modifier = Modifier.clickable {
                    onPlayerClicked(playerName)
                })

            }
        }
    }
}
