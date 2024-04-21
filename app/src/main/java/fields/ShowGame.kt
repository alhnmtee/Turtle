package fields

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ShowGame(
    letterCount: Int,
    gameOfPlayer: MutableState<Map<String, List<Int>>>,
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(16.dp)) // Boşluk eklendi

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Log.e(TAG, "ShowGame: $gameOfPlayer", )
                for (i in 0..<letterCount) {
                    if(gameOfPlayer.value.size>i){
                        WordField(gameOfPlayer.value.values.toList()[i], letterCount, firstText = gameOfPlayer.value.keys.toList()[i])

                    }
                    else{
                        WordField(List(letterCount) { 0 }, letterCount, firstText = "")
                    }
                }
            }


        }
    }
}
