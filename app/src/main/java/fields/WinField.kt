package fields

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.plcoding.onlinetictactoe.ui.theme.RoomsTheme

@Composable
fun WinField(
    playerWon: String?,
    playerScore: Int,
    opponentScore: Int,
    player: String,
    opponent: String,
    gameOfPlayer: MutableState<Map<String, List<Int>>>,
    opponentGameOfPlayer: MutableState<Map<String, List<Int>>>,
    letterCount: Int,
    indexOfWord: Int,
    onDuelButtonClick: () -> Unit,
    onExitButtonClick: () -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            if (playerWon == player) {
                Text("Tebrikler Kazandınız!", modifier = Modifier.padding(8.dp), color = Color.Green)
            } else {
                Text("Maalesef Kaybettiniz!", modifier = Modifier.padding(8.dp), color = Color.Red)
            }
            Text("Oyuncu Skorunuz: $playerScore", modifier = Modifier.padding(8.dp), color = Color.Green)
            Text("Rakip Skoru: $opponentScore", modifier = Modifier.padding(8.dp), color = Color.Red)


            Row(modifier = Modifier.padding(8.dp)) {
                if(playerWon!=player) {
                    Button(onClick = onDuelButtonClick) {
                        Text("Düello Gönder")
                    }
                }
                Spacer(modifier = Modifier.width(8.dp)) // Butonlar arasında boşluk bırakın
                Button(onClick = onExitButtonClick) {
                    Text("Çıkış Yap")
                }
            }
        }

        item {
            ShowGame(letterCount = letterCount, gameOfPlayer = gameOfPlayer)
        }

        item {
            ShowGame(letterCount = letterCount, gameOfPlayer = opponentGameOfPlayer)
        }
    }
}

@SuppressLint("UnrememberedMutableState", "SuspiciousIndentation")
@Preview
@Composable
fun WinFieldPreview() {
    val player = "Player"
    val opponent = "Opponent"
    val playerScore = 10
    val opponentScore = 5
    val gameOfPlayer = mutableStateOf(mapOf("a" to listOf(1, 2, 3)))
    val opponentGameOfPlayer = mutableStateOf(mapOf("b" to listOf(1, 2, 3)))
    val letterCount = 5
    val indexOfWord = 0



        WinField(
            playerWon = player,
            playerScore = playerScore,
            opponentScore = opponentScore,
            player = player,
            opponent = opponent,
            gameOfPlayer = gameOfPlayer,
            opponentGameOfPlayer = opponentGameOfPlayer,
            letterCount = letterCount,
            indexOfWord = indexOfWord,
            onDuelButtonClick = {},
            onExitButtonClick = {}

        )




}

