package fields

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

@Composable
fun GameField(
    letterCount: Int,
    indexOfWord: Int,
    gameOfPlayer: MutableState<Map<String, List<Int>>>,
    opponentGameOfPlayer: MutableState<Map<String, List<Int>>>,
    initialValue: String = "",
    playerScore: Int,
    showKeyboard: Boolean = true,
    playerWon: String?,
    //onReplayRequest: () -> Unit,
    submittedText: (String) -> Unit,


) {
    var text by remember { mutableStateOf(initialValue) }
    var showOpponentField by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Score: $playerScore",color= Color.Red)


                Button(onClick = { showOpponentField = !showOpponentField }) {
                    Text("Rakibi Göster")
                }
                /*if (!playerWon.isNullOrEmpty()) {
                    Button(onClick = onReplayRequest) { // Use onReplayRequest here
                        Text("Tekrar Oyun İsteği Gönder")
                    }
                }*/

                if (showOpponentField) {
                    OpponentGameField(letterCount, indexOfWord, opponentGameOfPlayer)
                }

                Spacer(modifier = Modifier.height(16.dp)) // Boşluk eklendi

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
                            WordField(List(letterCount) { 0 }, letterCount, firstText = text)
                        } else {
                            WordField(List(letterCount) { 0 }, letterCount, firstText = "")
                        }
                    }
                }
            }
        }
        if (showKeyboard) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Keyboard(onKeyPressed = { key ->
                        if (key == '⌫') {
                            if (text.isNotEmpty())
                                text = text.dropLast(1)
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
}

@Composable
fun OpponentGameField(
    letterCount: Int,
    indexOfWord: Int,
    gameOfPlayer: MutableState<Map<String, List<Int>>>,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
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
                } else {
                    WordField(List(letterCount) { 0 }, letterCount, firstText = "")
                }
            }
        }
    }
}