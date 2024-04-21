package fields

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun WinField(
    playerWon: String?,
    playerScore: Int,
    opponentScore: Int,
    player: String,
    opponent: String,
    playerWord : String,
    opponentWord : String,
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
            Text(text = "Sizin Oyununuz" , color = Color.Green)
            Text(text = "Aranan Kelime : $playerWord" ,color = Color.Green)
            ShowGame(letterCount = letterCount, gameOfPlayer = gameOfPlayer)
        }

        item {
            val fireStoreCollectionReference = FirebaseFirestore.getInstance().collection("Usernames")
            var playersUserName by remember { mutableStateOf<String>(opponent) }
            LaunchedEffect(playersUserName) {
                fireStoreCollectionReference.document(playersUserName).get().addOnSuccessListener { documentSnapshot ->
                    playersUserName = if (documentSnapshot.exists()) {
                        documentSnapshot.getString("username").toString()
                    } else {
                        playersUserName
                    }
                }
            }

            Text(text = "$playersUserName in Oyunu" ,color = Color.Red)
            Text(text = "Aranan Kelime : $opponentWord" ,color = Color.Red)
            ShowGame(letterCount = letterCount, gameOfPlayer = opponentGameOfPlayer)
        }
    }
}


