package fields

import android.content.ContentValues.TAG
import android.os.CountDownTimer
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun WordSelectionField(
    word: String = "",
    randomCharIndex : Int= -1,
    randomLetter :String = "",
    letterCount : Int,
    submittedText : (String) -> Unit,
){
    var text by remember { mutableStateOf(word) }
    var timerValue by remember { mutableStateOf(60) }

    val timer = object: CountDownTimer(60000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            timerValue = (millisUntilFinished / 1000).toInt()
        }

        override fun onFinish() {
            timerValue = 0
        }
    }

    LaunchedEffect(key1 = true) {
        timer.start()
    }



    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, bottom = 15.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

        )  {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            if(randomCharIndex!=-1){
                val listis = List(letterCount) { 0 }.toMutableList()
                listis[randomCharIndex] = 10
                var textis=""
                for (j in 0 until letterCount){
                    if(j==randomCharIndex)
                        textis+=randomLetter
                    else if (j<text.length){
                        textis+=text[j]
                    }
                    else if(j>randomCharIndex){
                        textis+=""
                    }
                    else{
                        textis+=" "
                    }

                }
                if(text.length == randomCharIndex){
                    text = textis
                }

                WordField(scoreOfTheWord = List(letterCount){0}, letterCount, firstText = textis)
            }
            else{
                WordField(scoreOfTheWord = List(letterCount){0},letterCount,firstText=text)
                Text(text = "Remaining time: $timerValue seconds",color= Color.Cyan)
            }
        }
        Column (modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            Keyboard(onKeyPressed = { key ->
                Log.e(TAG, "basılan tuş: $key", )
                if(key == '⌫'){
                    if (text.isNotEmpty() && randomCharIndex!=-1 && (text.length== randomCharIndex +1 || text.length== randomCharIndex +2))
                    {
                        text = text.dropLast(2)
                        Log.e(TAG, "eğer denkse yeşsinden silindi" )
                    }

                    else if (text.isNotEmpty()){
                        text = text.dropLast(1)
                        Log.e(TAG, "Normal silinmeden silindi " )
                    }
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

