package fields

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GameField(
    letterCount: Int,
    indexOfWord: Int,
    gameOfPlayer: MutableState<Map<String, List<Int>>>,
    opponentGame :MutableState<Map<String, List<Int>>>,
    initialValue: String = "",
    playerScore: Int,
    playerWon: String?,
    randomCharIndex: Int=-1,
    randomWord: String="",
    showQuitButton: Boolean = true,
    //onReplayRequest: () -> Unit,
    onButtonClick: () -> Unit,
    submittedText: (String) -> Unit,
) {
    var text by remember { mutableStateOf(initialValue) }
    var showOtherPlayer by remember { mutableStateOf(false) }
    var showKeyboard by remember { mutableStateOf(true) }
    // var openDialog by remember { mutableStateOf(false) }

    /*if (openDialog) {
        androidx.compose.material.AlertDialog(
            onDismissRequest = { openDialog = false },
            title = { Text("Oyundan Çıkmak İstiyor Musunuz?Kaybedeceksiniz.") },
            confirmButton = {
                Button(onClick = {
                    openDialog = false
                }) {
                    Text("Evet")
                }
            },
            dismissButton = {
                Button(onClick = { openDialog = false }) {
                    Text("Hayır")
                }
            }
        )
    }

    BackHandler {
        openDialog = true
    }*/
    Column(
        modifier = Modifier.fillMaxSize()
    ) {








        LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                item{
                    Button(onClick =  {showOtherPlayer = !showOtherPlayer
                        showKeyboard=!showKeyboard
                    }) {
                        Text(text = "Rakibi Göster / Gizle")
                    }

                }
                item{
                    if(showOtherPlayer){
                        ShowGame(letterCount,opponentGame)
                    }
                }
                item{
                    Text("Score: $playerScore",color= Color.Red)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.width(16.dp))
                        if (showQuitButton) {
                            Button(onClick = onButtonClick) {
                                Text("Çıkış Yap")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item{
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        for (i in 0 until letterCount) {
                            if (i < indexOfWord) {
                                WordField(
                                    gameOfPlayer.value.values.toList()[i],
                                    letterCount,
                                    firstText = gameOfPlayer.value.keys.toList()[i]
                                )
                            } else if (i == indexOfWord) {
                                if(randomCharIndex!=-1){
                                    val listis = List(letterCount) { 0 }.toMutableList()
                                    listis[randomCharIndex] = 10
                                    var textis=""
                                    for (j in 0 until letterCount){
                                        if(j==randomCharIndex)
                                            textis+=randomWord[randomCharIndex]
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

                                    WordField(listis, letterCount, firstText = textis)
                                }
                                else{
                                    WordField(List(letterCount) { 0 }, letterCount, firstText = text)
                                }


                            } else {
                                if(randomCharIndex!=-1){
                                    val listis = List(letterCount) { 0 }.toMutableList()
                                    listis[randomCharIndex] = 10
                                    var textis=""
                                    for (j in 0 until letterCount){
                                        if(j==randomCharIndex)
                                            textis+=randomWord[randomCharIndex]
                                        else
                                            textis+=" "

                                    }
                                    //text = text.substring(0,randomCharIndex)+randomWord[randomCharIndex]+text.substring(randomCharIndex+1)
                                    WordField(listis, letterCount, firstText = textis)

                                }
                                else{
                                    WordField(List(letterCount) { 0 }, letterCount, firstText = "")}

                            }
                        }
                    }
                }

            }

        if (showKeyboard) {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Keyboard(onKeyPressed = { key ->
                        if (key == '⌫') {
                            if (text.isNotEmpty() && randomCharIndex!=-1 && (text.length== randomCharIndex +1 || text.length== randomCharIndex +2))
                            {
                                text = text.dropLast(2)
                                Log.e(TAG, "eğer denkse yeşsinden silindi" )
                            }

                            else if (text.isNotEmpty()){
                                text = text.dropLast(1)
                                Log.e(TAG, "Normal silinmeden silindi " )
                            }


                        } else if (key == '⏎') {
                            if (text.length == letterCount) {
                                submittedText(text)
                                text = ""
                            }
                        } else {
                            if (text.length < letterCount)
                                text += key
                        }
                    })
                }

        }

    }
}

