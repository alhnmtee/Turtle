package fields
import android.content.ContentValues
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
import androidx.compose.ui.unit.dp



@Composable
fun GameField(
    letterCount : Int,
    indexOfWord : Int,
    gameOfPlayer : Map<String,List<Int>>,
    initialValue :String = "",
    submittedText : (String) -> Unit,

    ) {
    var text by remember { mutableStateOf(initialValue) }
    // Display the characters of the word in individual boxes
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(top = 15.dp, bottom = 15.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,


    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(top=5.dp, bottom = 5.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            for(i in 0..<letterCount){
                if(i<indexOfWord){
                    WordField(gameOfPlayer.values.toList()[i],letterCount, firstText = gameOfPlayer.keys.toList()[i])
                }
                else if( i == indexOfWord){
                    WordField(List(10){0},letterCount, firstText = text)
                }
                else{
                    WordField(List(10){0},letterCount, firstText = "")
                }

            }

        }
        Column (modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            Keyboard(onKeyPressed = { key ->
                Log.e(ContentValues.TAG, "basılan tuş: $key", )
                if(key == '⌫'){
                    if(text.isNotEmpty())
                        text = text.dropLast(1)

                }
                else if(key == '⏎'){
                    if(text.length==letterCount){
                        submittedText(text)
                        text = ""
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



