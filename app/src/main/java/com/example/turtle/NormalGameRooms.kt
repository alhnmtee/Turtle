package com.example.turtle

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.turtle.databinding.NormalGameRoomsBinding
import com.google.firebase.auth.FirebaseAuth
import com.plcoding.onlinetictactoe.ui.theme.RoomsTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback
import data.RoomViewModel
import data.RoomViewModelFactory
import fields.GameField
import fields.RoomField
import fields.WordSelectionField
import java.io.BufferedReader
import java.io.InputStreamReader


@AndroidEntryPoint
class NormalGameRooms : Fragment(R.layout.normal_game_rooms) {
    private var _binding: NormalGameRoomsBinding? = null
    private val binding get() = _binding!!
    var mode : String = ""
    var lc : Int = 0

    private val wordsList = /*readWordsFromFile(this.context)*/ listOf("BURAK","KURAK")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mode = loadGameMode().toString()
        lc = loadWordSize()
    }

    private val viewModel: RoomViewModel by viewModels()

    //compose arayüzünün yazıldığı fonksiyon
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.normal_game_rooms, container, false)
        val composeView = view.findViewById<ComposeView>(R.id.compose_view)


        composeView.apply {
            // Dispose of the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                RoomsTheme {
                    val viewModel by viewModels<RoomViewModel>(
                        extrasProducer = {
                            defaultViewModelCreationExtras.withCreationCallback<
                                    RoomViewModelFactory> { factory ->
                                factory.create(mode,lc)
                            }
                        }
                    )
                    val state by viewModel.state.collectAsState()
                    var showGameScreen by remember { mutableStateOf(false) }

                    if(state.isGamePlaying){
                        val playerGame : Map<String,List<Int>> =
                            when(FirebaseAuth.getInstance().uid){
                                state.player1Id -> state.player1Game
                                state.player2Id -> state.player2Game
                                else -> {state.player1Game}
                            }

                        val playerWord : String =
                            when(FirebaseAuth.getInstance().uid){
                                state.player1Id -> state.player1Word
                                state.player2Id -> state.player2Word
                                else -> {state.player1Word}
                            }

                        LaunchedEffect(state.player1Word, state.player2Word) {
                            if (state.player1Word.isNotEmpty() && state.player2Word.isNotEmpty()) {
                                showGameScreen = true
                            }
                        }

                        Log.e(TAG, "oyun : $playerGame ", )
                        if (showGameScreen) {
                            GameField(letterCount = lc, indexOfWord = playerGame.size, gameOfPlayer = playerGame) {
                                    submittedText ->
                                val response = viewModel.sendWord(submittedText,wordsList)
                                Log.d("WordSelectionField", "Submitted word: $submittedText")
                                Log.d("WordSelectionField", "Server response: $response")

                                Log.d("WordSelectionField", "State: $state")
                            }
                            return@RoomsTheme
                        }

                        if(mode == "normal" &&
                            when (FirebaseAuth.getInstance().uid) {
                                state.player1Id -> state.player2Word!=" "
                                state.player2Id -> state.player1Word!=" "
                                else -> false
                            }
                        ){
                            WordSelectionField(letterCount = lc) {
                                submittedText ->
                                val response = viewModel.setWordForOtherPlayer(submittedText,wordsList)
                                Log.d("WordSelectionField", "Submitted word: $submittedText")
                                Log.d("WordSelectionField", "Server response: $response")

                                Log.d("WordSelectionField", "State: $state")

                            }
                            return@RoomsTheme
                        }


                        LaunchedEffect(state.player1Word, state.player2Word) {
                            Log.d("RoomViewModel", "player1Word: ${state.player1Word}, player2Word: ${state.player2Word}")
                        }

                        //state ' e göre kulllanıcı , kullanıcı 1 mi yoksa 2 mi ona bakılıcak .
                        //kmasndasdnksakd(){
                        // caturıoı ->
                        //setWordForOtherPlayer(caturıoı)
                        //}
                        //kelime girişi kelime belirleme için ayrı fonksiyon olarak yapılırsa daha iyi
                        //açılcak composable fonksiyon şeysine doğru veriler gönderilecek
                        //composable şeysinden gelen veriler server' a iletilecek


                        return@RoomsTheme

                    }
                    if (state.rejectedPlayers.contains(FirebaseAuth.getInstance().uid)) {
                        AlertDialog(
                            onDismissRequest = {

                            },
                            title = {
                                Text(text = "Reddedildiniz")
                            },
                            text = {
                                Text("Gönderdiğiniz istek reddedildi")
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        val senderId = FirebaseAuth.getInstance().uid // Get the ID of the user who sent the request
                                        viewModel.gotDenied()
                                    }
                                ) {
                                    Text("Tamam")
                                }
                            },

                        )
                    }

                    if (state.requests.containsValue(FirebaseAuth.getInstance().uid)) {
                        AlertDialog(
                            onDismissRequest = {

                            },
                            title = {
                                Text(text = "Game Request")
                            },
                            text = {
                                Text("You have a game request. Do you want to accept?")
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        val senderId = FirebaseAuth.getInstance().uid // Get the ID of the user who sent the request
                                        val receiverId = state.requests.entries.find{ it.value == senderId}?.key
                                        if (senderId != null && receiverId != null) {
                                            viewModel.startGame(senderId, receiverId)
                                        }
                                        Log.e(TAG, "isteği kabul et: ${state}", )
                                    }
                                ) {
                                    Text("Kabul Et")
                                }
                            },
                            dismissButton = {
                                Button(
                                    onClick = {
                                        val senderId = FirebaseAuth.getInstance().uid
                                        val receiverId = state.requests.entries.find{ it.value == senderId}?.key
                                        if (senderId != null && receiverId != null) {
                                            viewModel.denyGame(senderId,receiverId)

                                        }
                                        Log.e(TAG, "isteği red et: ${state}", )

                                    }
                                ) {
                                    Text("Reddet")
                                }
                            }
                        )
                    }

                    val isConnecting by viewModel.isConnecting.collectAsState()
                    val showConnectionError by viewModel.showConnectionError.collectAsState()

                    Log.e(TAG, "onCreateView: ${state.connectedPlayers}", )

                    Log.e(TAG, "onCreateView: ${state}", )
                    //hata var mı diye
                    if(showConnectionError) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Server'la bağlantı kurulamadı",
                                color = MaterialTheme.colors.error
                            )
                        }
                        return@RoomsTheme
                    }
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        //oyuncuların sıralandığı yer
                        Log.e(TAG, "onCreateView: ${state.connectedPlayers}", )
                        if(state.connectedPlayers.isNotEmpty()){
                            RoomField(state = state){playerName ->
                                // Handle player clicks here
                                if(!state.requests.containsKey(FirebaseAuth.getInstance().uid))
                                    viewModel.sendGameRequest(playerName)
                                Log.d(TAG, "Player clicked: $playerName")
                            }
                        }

                        //bağlanılıyor dönen şey
                        if (isConnecting) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .align(Alignment.Center)
                            )
                        }
                    }




                }

            }
        }
        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = NormalGameRoomsBinding.bind(view)



        val wordSize = loadWordSize()
        val gameMode = loadGameMode().toString()

        binding.gameModeAndWordSizeTextView.text = "Selected game mode: $gameMode, Word size: $wordSize"
    }

    private fun loadWordSize(): Int {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return 0
        return sharedPref.getInt(getString(R.string.saved_word_size), 0)
    }

    private fun loadGameMode(): String? {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        return sharedPref?.getString(getString(R.string.saved_game_mode), "")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        //viewModel.onCleared()
    }
}


private fun readWordsFromFile(context: Context?): List<String> {
    val wordsList = mutableListOf<String>()

    try {
        val inputStream = context!!.assets.open("assets/kelimeler.txt")

        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            var line: String? = reader.readLine()
            while (line != null) {
                wordsList.add(line)
                line = reader.readLine()
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return wordsList
}