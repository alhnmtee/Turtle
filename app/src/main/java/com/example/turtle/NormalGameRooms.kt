package com.example.turtle

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.navigation.compose.rememberNavController
import com.example.turtle.databinding.NormalGameRoomsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.plcoding.onlinetictactoe.ui.theme.RoomsTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback
import data.RoomViewModel
import data.RoomViewModelFactory
import fields.GameField
import fields.RoomField
import fields.WinField
import fields.WordSelectionField
import kotlinx.coroutines.delay
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.random.Random


@AndroidEntryPoint
class NormalGameRooms : Fragment(R.layout.normal_game_rooms) {
    private var _binding: NormalGameRoomsBinding? = null
    private val binding get() = _binding!!
    var mode : String = ""
    var lc : Int = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mode = loadGameMode().toString()
        lc = loadWordSize()
    }

    private val viewModel: RoomViewModel by viewModels()

    //compose arayüzünün yazıldığı fonksiyon
    @SuppressLint("UnrememberedMutableState")
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
                    val wordsList1 = readWordsFromFile(this.context, lc).filter{it.length == lc}
                    val navController = rememberNavController()
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
                        val playerGame: MutableState<Map<String, List<Int>>> = mutableStateOf(
                            when(FirebaseAuth.getInstance().uid){
                                state.player1Id -> state.player1Game
                                state.player2Id -> state.player2Game
                                else -> {state.player1Game}
                            }
                        )
                        val opponentGame: MutableState<Map<String, List<Int>>> = mutableStateOf(
                            when(FirebaseAuth.getInstance().uid) {
                                state.player1Id -> state.player2Game
                                state.player2Id -> state.player1Game
                                else -> state.player2Game
                            }
                        )
                        val currentUserId = FirebaseAuth.getInstance().uid
                        val navController = rememberNavController()


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

                            var secondsLeft by remember { mutableStateOf(10) }
                            var isTimerRunning by remember { mutableStateOf(true) }

                            LaunchedEffect(Unit) {
                                while (isTimerRunning && secondsLeft > 0) {
                                    delay(1000)
                                    secondsLeft--
                                }
                                if (isTimerRunning) {
                                    val senderId = FirebaseAuth.getInstance().uid
                                    val receiverId = state.requests.entries.find{ it.value == senderId}?.key
                                    if (senderId != null && receiverId != null) {
                                        viewModel.denyGame(senderId,receiverId)

                                    }
                                }
                            }

                            AlertDialog(
                                onDismissRequest = {

                                },
                                title = {
                                    Text(text = "Oyun isteği")
                                },
                                text = {
                                    val fireStoreCollectionReference = FirebaseFirestore.getInstance().collection("Usernames")
                                    var sendingPlayerUid = ""
                                    state.requests.entries.forEach {
                                        if(it.value == FirebaseAuth.getInstance().uid){
                                            sendingPlayerUid=it.key
                                        }
                                        else{

                                        }
                                    }
                                    var playersUserName by remember { mutableStateOf<String>(sendingPlayerUid) }
                                    LaunchedEffect(playersUserName) {
                                        fireStoreCollectionReference.document(playersUserName).get().addOnSuccessListener { documentSnapshot ->
                                            playersUserName = if (documentSnapshot.exists()) {
                                                documentSnapshot.getString("username").toString()
                                            } else {
                                                playersUserName
                                            }
                                        }
                                    }
                                    Text("$playersUserName size bir Oyun isteği gönderdi!")
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
                                        Text("Kabul Et ($secondsLeft saniye kaldı)")
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


                        if (!state.playerWon.isNullOrEmpty()) {
                            if (state.playerWon == currentUserId) {
                                Toast.makeText(context, "Kazandınız, Tebrikler!", Toast.LENGTH_LONG).show()
                                WinField(
                                    playerWon = state.playerWon,
                                    playerScore = when(currentUserId){
                                        state.player1Id -> state.player1Score
                                        state.player2Id -> state.player2Score
                                        else -> 0
                                    },
                                    opponentScore = when(currentUserId){
                                        state.player1Id -> state.player2Score
                                        state.player2Id -> state.player1Score
                                        else -> 0
                                    },
                                    player = currentUserId,
                                    opponent = when(currentUserId){
                                        state.player1Id -> state.player2Id
                                        state.player2Id -> state.player1Id
                                        else -> ""
                                    },
                                    gameOfPlayer = playerGame,
                                    opponentGameOfPlayer = opponentGame,
                                    letterCount = lc,
                                    indexOfWord =playerGame.value.size,
                                    playerWord = when(currentUserId){
                                          state.player1Id -> state.player1Word
                                          state.player2Id -> state.player2Word
                                          else -> ""
                                    },
                                    opponentWord = when(currentUserId){
                                        state.player1Id -> state.player2Word
                                        state.player2Id -> state.player1Word
                                        else -> ""
                                    },
                                    onDuelButtonClick = { /*TODO*/ },
                                    onExitButtonClick = {
                                        val uid = FirebaseAuth.getInstance().uid
                                        if (uid != null) {
                                            viewModel.disconnectFromGame(uid)
                                        }
                                        Log.e(TAG, "onCreateView: ${state}", )
                                    }
                                )
                            } else {
                                Toast.makeText(context, "Kaybettiniz, Üzgünüm!", Toast.LENGTH_LONG).show()
                                if (currentUserId != null) {
                                    WinField(
                                        playerWon = state.playerWon,
                                        playerScore = when(currentUserId){
                                            state.player1Id -> state.player1Score
                                            state.player2Id -> state.player2Score
                                            else -> 0
                                        },
                                        opponentScore = when(currentUserId){
                                            state.player1Id -> state.player2Score
                                            state.player2Id -> state.player1Score
                                            else -> 0
                                        },
                                        player = currentUserId,
                                        opponent = when(currentUserId){
                                            state.player1Id -> state.player2Id
                                            state.player2Id -> state.player1Id
                                            else -> ""
                                        },
                                        gameOfPlayer = playerGame,
                                        opponentGameOfPlayer = opponentGame,
                                        letterCount = lc,
                                        indexOfWord = playerGame.value.size,
                                        playerWord = when(currentUserId){
                                            state.player1Id -> state.player1Word
                                            state.player2Id -> state.player2Word
                                            else -> ""
                                        },
                                        opponentWord = when(currentUserId){
                                            state.player1Id -> state.player2Word
                                            state.player2Id -> state.player1Word
                                            else -> ""
                                        },
                                        onDuelButtonClick = {
                                            val senderId = FirebaseAuth.getInstance().uid
                                            val receiverId = when (senderId) {
                                                state.player1Id -> state.player2Id
                                                state.player2Id -> state.player1Id
                                                else -> null
                                            }
                                            if (receiverId != null && !state.requests.containsKey(
                                                    senderId
                                                )
                                            ) {
                                                viewModel.sendGameRequest(receiverId)
                                            }
                                        },
                                        onExitButtonClick = {
                                            val uid = FirebaseAuth.getInstance().uid
                                            if (uid != null) {
                                                viewModel.disconnectFromGame(uid)
                                            }
                                            Log.e(TAG, "onCreateView: ${state}", )
                                        }
                                    )
                                }
                            }
                            return@RoomsTheme
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
                            else{
                                showGameScreen = false
                            }
                        }

                        Log.e(TAG, "oyun : $playerGame ", )
                        if (showGameScreen) {
                            val navController = rememberNavController()
                            val playerScore: Int =
                                when (FirebaseAuth.getInstance().uid) {
                                    state.player1Id -> state.player1Score
                                    state.player2Id -> state.player2Score
                                    else -> 0
                                }
                            var countdown by remember { mutableStateOf(60) }

                            LaunchedEffect(key1 = countdown) {
                                while (countdown > 0) {
                                    delay(1000L)
                                    countdown--
                                }

                            }
                            if (countdown <= 0) {
                                var secondsLeft by remember { mutableStateOf(10) }
                                var isTimerRunning by remember { mutableStateOf(true) }
                                var openDialog by remember { mutableStateOf(true) }

                                LaunchedEffect(Unit) {
                                    while (isTimerRunning && secondsLeft > 0) {
                                        delay(1000)
                                        secondsLeft--
                                    }
                                    if (isTimerRunning) {
                                        val uid = FirebaseAuth.getInstance().uid
                                        if (uid != null) {
                                            viewModel.playerWon(uid)
                                            viewModel.disconnectFromGame(uid)
                                            navController.popBackStack()
                                        }
                                    }
                                }
                                if(openDialog){
                                    AlertDialog(
                                        onDismissRequest = { isTimerRunning = false },
                                        title = { Text(text = "Aktiflik") },
                                        text = { Text("Lütfen oyuna devam ediniz.Bağlantınız kesilecek") },
                                        confirmButton = {
                                            Button(
                                                onClick = { isTimerRunning = false
                                                    openDialog = false
                                                    countdown = 60
                                                }
                                            ) {
                                                Text("Tamam($secondsLeft saniye kaldı)")
                                            }
                                        },
                                    )
                                }
                            }



                            var openDialog by remember { mutableStateOf(false) }


                            if(openDialog){
                                AlertDialog(
                                    onDismissRequest = { openDialog = false },
                                    title = { Text("Oyundan Çıkmak İstiyor Musunuz?") },
                                    confirmButton = {
                                        Button(onClick = {
                                            val otherPlayerId = when (FirebaseAuth.getInstance().uid) {
                                                state.player1Id -> state.player2Id
                                                state.player2Id -> state.player1Id
                                                else -> null
                                            }
                                            if (otherPlayerId != null) {
                                                viewModel.playerWon(otherPlayerId)
                                                viewModel.disconnectFromGame(otherPlayerId)
                                                navController.popBackStack()
                                            }
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

                            }





                            GameField(
                                    letterCount = lc,
                                    indexOfWord = playerGame.value.size,
                                    gameOfPlayer = playerGame,
                                    playerScore = playerScore,
                                    opponentGame = opponentGame,
                                    playerWon = state.playerWon,
                                    randomCharIndex = if(state.randomCharIndex != -1) state.randomCharIndex else -1,
                                    randomWord = if(state.randomCharIndex != -1) state.player1Word else "",
                                    onButtonClick = {
                                        openDialog = true
                                    },
                                    submittedText = { submittedText ->
                                        if(state.playerWon!=FirebaseAuth.getInstance().uid){
                                            val response = viewModel.sendWord(submittedText,wordsList1)
                                            if (!response) {
                                                Toast.makeText(context, "Lütfen geçerli bir kelime giriniz.", Toast.LENGTH_LONG).show()
                                            }
                                            if(response){
                                                countdown = 60
                                            }
                                        }

                                        Log.d("WordSelectionField", "Submitted word: $submittedText")
                                        Log.d("WordSelectionField", "State: $state")
                                    }


                                )




                            return@RoomsTheme
                        }

                        if((mode == "normal" || mode == "letter") &&
                            when (FirebaseAuth.getInstance().uid) {
                                state.player1Id -> state.player2Word!=" "
                                state.player2Id -> state.player1Word!=" "
                                else -> false
                            }
                        ){
                            //Kelime girmede Hiçbir şey yapılmadğı zaman yapılacaklar.
                            var timerValue by remember { mutableStateOf(60) }
                            var randomLetter by remember { mutableStateOf("") }
                            var randomLetterIndex by remember { mutableStateOf(-1) }


                            if(mode == "letter" && randomLetterIndex==-1){
                                val randomWord :String = wordsList1.get(Random.nextInt(0,wordsList1.size))
                                randomLetterIndex = Random.nextInt(0,lc)
                                randomLetter = randomWord[randomLetterIndex].toString()
                            }



                            val timer = object: CountDownTimer(60000, 1000) {
                                override fun onTick(millisUntilFinished: Long) {
                                    timerValue = (millisUntilFinished / 1000).toInt()
                                }

                                override fun onFinish() {
                                    timerValue = 0
                                    if(state.player1Word.isEmpty() && state.player2Word.isEmpty()){
                                        viewModel.disconnectFromGame(FirebaseAuth.getInstance().uid!!)
                                    } else if(state.player1Word.isNotEmpty() && state.player2Word.isEmpty()){
                                        viewModel.playerWon(state.player2Id)
                                        viewModel.disconnectFromGame(state.player2Id)
                                    } else if(state.player1Word.isEmpty() && state.player2Word.isNotEmpty()){
                                        viewModel.playerWon(state.player1Id)
                                        viewModel.disconnectFromGame(state.player1Id)
                                    }
                                }
                            }

                            LaunchedEffect(key1 = true) {
                                timer.start()
                            }




                            WordSelectionField(letterCount = lc, randomLetter = randomLetter, randomCharIndex = randomLetterIndex) {
                                    submittedText ->
                                val response = viewModel.setWordForOtherPlayer(submittedText,wordsList1)
                                Log.d("WordSelectionField", "Submitted word: $submittedText")
                                Log.d("WordSelectionField", "Server response: $response")
                                if (!response) {
                                    Toast.makeText(context, "Lütfen geçerli bir kelime giriniz.", Toast.LENGTH_LONG).show()
                                }
                                else
                                    Toast.makeText(context, "Kelime gönderildi.Lütfen bekleyiniz", Toast.LENGTH_LONG).show()
                                Log.d("WordSelectionField", "State: $state")
                            }
                            return@RoomsTheme
                        }








                        LaunchedEffect(state.player1Word, state.player2Word) {
                            Log.d("RoomViewModel", "player1Word: ${state.player1Word}, player2Word: ${state.player2Word}")
                        }


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

                        var secondsLeft by remember { mutableStateOf(10) }
                        var isTimerRunning by remember { mutableStateOf(true) }

                        LaunchedEffect(Unit) {
                            while (isTimerRunning && secondsLeft > 0) {
                                delay(1000)
                                secondsLeft--
                            }
                            if (isTimerRunning) {
                                val senderId = FirebaseAuth.getInstance().uid
                                val receiverId = state.requests.entries.find{ it.value == senderId}?.key
                                if (senderId != null && receiverId != null) {
                                    viewModel.denyGame(senderId,receiverId)

                                }
                            }
                        }

                        AlertDialog(
                            onDismissRequest = {

                            },
                            title = {
                                Text(text = "Oyun isteği")
                            },
                            text = {
                                val fireStoreCollectionReference = FirebaseFirestore.getInstance().collection("Usernames")
                                var sendingPlayerUid = ""
                                state.requests.entries.forEach {
                                    if(it.value == FirebaseAuth.getInstance().uid){
                                        sendingPlayerUid=it.key
                                    }
                                    else{

                                    }
                                }
                                var playersUserName by remember { mutableStateOf<String>(sendingPlayerUid) }
                                LaunchedEffect(playersUserName) {
                                    fireStoreCollectionReference.document(playersUserName).get().addOnSuccessListener { documentSnapshot ->
                                        playersUserName = if (documentSnapshot.exists()) {
                                            documentSnapshot.getString("username").toString()
                                        } else {
                                            playersUserName
                                        }
                                    }
                                }
                                Text("$playersUserName size bir Oyun isteği gönderdi!")
                            },
                            confirmButton = {

                                    Button(
                                        onClick = {
                                            val senderId = FirebaseAuth.getInstance().uid
                                            val receiverId = state.requests.entries.find{ it.value == senderId}?.key
                                            if (senderId != null && receiverId != null) {
                                                viewModel.startGame(senderId, receiverId)
                                            }
                                            Log.e(TAG, "isteği kabul et: ${state}", )
                                        }
                                    ) {
                                        Text("Kabul Et ($secondsLeft saniye kaldı)")
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
                            val currentUserId = FirebaseAuth.getInstance().uid
                            val otherPlayers = state.connectedPlayers.filter { it != currentUserId }
                            RoomField(state = state.copy(connectedPlayers = otherPlayers),mode=mode, letterCount = lc){playerName ->
                                // Handle player clicks here
                                if(!state.requests.containsKey(currentUserId) && !state.playersCurrentlyPlaying.contains(playerName))
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

        //binding.gameModeAndWordSizeTextView.text = "Selected game mode: $gameMode, Word size: $wordSize"
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
        viewModel.onCleared()
    }
}


private fun readWordsFromFile(context: Context?, letterCount: Int): List<String> {
    val wordsList = mutableListOf<String>()

    try {
        val inputStream = context!!.assets.open("kelimelerB.txt")

        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            var line: String? = reader.readLine()
            while (line != null) {
                if (line.length == letterCount) {
                    wordsList.add(line)

                }
                line = reader.readLine()
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()

    }

    Log.d("readWordsFromFile", "Total words read: ${wordsList.size}")
    return wordsList
}
