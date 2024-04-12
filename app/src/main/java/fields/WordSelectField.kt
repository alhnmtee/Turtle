package fields

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun WordSelectionField(
    word: String = "",
    letterCount : Int,
    submittedText : (String) -> Unit,
){
    var text by remember { mutableStateOf(word) }

    // Display the characters of the word in individual boxes
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, bottom = 15.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,


        ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            WordField(scoreOfTheWord = List(letterCount){0},letterCount,firstText=text)
        }
        Column (modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            Keyboard(onKeyPressed = { key ->
                Log.e(TAG, "basılan tuş: $key", )
                if(key == '⌫'){
                    if(text.isNotEmpty())
                        text = text.dropLast(1)

                }
                else if(key == '⏎'){
                    if(text.length==letterCount) {
                        submittedText(text)
                    }
                }
                else {
                    if(text.length < letterCount)
                        text += key
                }

            })
        }



    }
}
@Preview
@Composable
fun PreviewWordSelectionField() {
    WordSelectionField(
        word = "test",
        letterCount = 5,
        submittedText = { }
    )
}

