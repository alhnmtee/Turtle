package gameField

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import data.GameState


//TODO wordle oynanış kısmı burada olucak
@Composable
fun WordleField (
    state : GameState,
    modifier:Modifier = Modifier,
){
    Canvas(
        modifier = modifier,
        onDraw = {
            drawCircle(color = Color.Blue,
                center = Offset(x=50.0f,y=50.0f),
                style = Stroke(
                    width = 3.dp.toPx()
                )
            )
        }
    )

}