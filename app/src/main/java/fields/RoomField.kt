package fields

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.classes.RoomState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

//TODO wordle oynanış kısmı burada olucak
@Composable
fun RoomField (
    state: RoomState,
    mode : String,
    letterCount : Int,
    onPlayerClicked : (String) -> Unit
){
    Column(modifier = Modifier.fillMaxSize(),) {
        Text(text = "$mode    /    $letterCount",color= Color.White , textAlign = TextAlign.Center ,style = TextStyle(fontSize = 20.sp)
        , modifier =  Modifier.fillMaxWidth())
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement =Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            item{
                val fireStoreCollectionReference = FirebaseFirestore.getInstance().collection("Usernames")
                var playersUserName by remember { mutableStateOf<String>(FirebaseAuth.getInstance().uid.toString()) }
                LaunchedEffect(playersUserName) {
                    fireStoreCollectionReference.document(playersUserName).get().addOnSuccessListener { documentSnapshot ->
                        playersUserName = if (documentSnapshot.exists()) {
                            documentSnapshot.getString("username").toString()
                        } else {
                            playersUserName
                        }
                    }
                }
                Text("Diğer oyunculara $playersUserName adıyla Görünüyorsunuz",modifier = Modifier.padding(8.dp), color = Color.White)
                Text("Bağlı Oyuncular :", modifier = Modifier.padding(8.dp), color = Color.White)
            }

            item{
                Column(
                    modifier = Modifier.padding(vertical = 15.dp)
                ) {
                    val fireStoreCollectionReference = FirebaseFirestore.getInstance().collection("Usernames")

                    state.connectedPlayers.forEach { playerName ->
                        val playerStatus = if (state.playersCurrentlyPlaying.contains(playerName)) "Oyunda" else if (state.requests.containsKey(FirebaseAuth.getInstance().uid)
                            && state.requests[FirebaseAuth.getInstance().uid] == playerName) "İstek Gönderildi" else "Lobide"
                        var secondsLeft by remember { mutableStateOf(10) }
                        var isTimerRunning by remember { mutableStateOf(false) }


                        if(isTimerRunning){
                            LaunchedEffect(Unit) {
                                while (isTimerRunning && secondsLeft > 0) {
                                    delay(1000)
                                    secondsLeft--
                                }
                                isTimerRunning=false
                            }
                        }else{
                            secondsLeft=10
                        }



                        var username by remember { mutableStateOf<String>(playerName) }
                        LaunchedEffect(playerName) {
                            fireStoreCollectionReference.document(playerName).get().addOnSuccessListener { documentSnapshot ->
                                username = if (documentSnapshot.exists()) {
                                    documentSnapshot.getString("username").toString()
                                } else {
                                    playerName
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .border(BorderStroke(2.dp, color = Color.Cyan))
                                .clickable { onPlayerClicked(playerName) }
                        ){
                            Text(
                                text = "$username ( $playerStatus ${if(playerStatus == "İstek Gönderildi") secondsLeft.toString() else ""})",
                                color = if(playerStatus=="Oyunda") Color.Red else if(playerStatus=="İstek Gönderildi") Color.Green else Color.White,
                                modifier = Modifier.padding(8.dp)
                            )
                        }


                        if(playerStatus!="İstek Gönderildi" && isTimerRunning){
                            isTimerRunning=false
                            secondsLeft=10
                        }
                        else if(playerStatus=="İstek Gönderildi" && !isTimerRunning){
                            secondsLeft=10
                            isTimerRunning=true
                        }


                    }

                }
            }

        }
    }


}
