package roomField

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
    modifier: Modifier = Modifier,
){
    // Odadaki oyuncların gösterilip , tıklama işlemi filan yaplıacağı yer burası
    Column(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // You can draw any custom graphics here
            // For simplicity, let's draw a background color
            drawRect(color = Color.LightGray)
        }

        // Display connected players
        Text("Connected Players:", modifier = Modifier.padding(8.dp))
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            state.connectedPlayers.forEach { playerName ->
                Text(text = playerName , color = Color.Black)

            }
        }
    }
}
