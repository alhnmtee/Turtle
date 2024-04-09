package fields

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Keyboard(
    onKeyPressed: (Char) -> Unit
) {
    val keyboardLayout = listOf(
        listOf('E', 'R', 'T', 'Y', 'U','I', 'O', 'P', 'Ğ','Ü'),
        listOf('A', 'S', 'D', 'F', 'G','H', 'J', 'K', 'L','Ş','İ'),
        listOf( '⌫', 'Z', 'C', 'V', 'B','N', 'M', 'Ö', 'Ç','⏎'),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(2.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        keyboardLayout.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                row.forEach { char ->
                    KeyboardKey(
                        char = char,
                        onKeyPressed = onKeyPressed
                    )
                }
            }
            //Spacer(modifier = Modifier.height(8.dp))
        }

    }
}

@Composable
private fun KeyboardKey(
    char: Char,
    onKeyPressed: (Char) -> Unit
) {
    Button(
        onClick = { onKeyPressed(char) },
        modifier = Modifier
            .height(50.dp)
            .width(32.dp),
        contentPadding = PaddingValues(0.dp)

    ) {
        Text(
            text = char.toString(),
            modifier = Modifier.size(20.dp),
            style = TextStyle.Default.copy(fontFamily = FontFamily.SansSerif , fontSize = 14.sp),
            textAlign = TextAlign.Center
        )
    }
}