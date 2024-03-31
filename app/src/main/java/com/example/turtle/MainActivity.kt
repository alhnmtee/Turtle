package com.example.turtle

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import data.WordleViewModel


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            val viewModel = hiltViewModel<WordleViewModel>()
            val state by viewModel.state.collectAsState()
            val isConnecting by viewModel.isConnecting.collectAsState()
            val showConnectionError by viewModel.showConnectionError.collectAsState()

            if(showConnectionError){
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center

                ){
                    Text(
                        color = Color.White,
                        text = "Serverla bağlantı kurulamadı"
                    )
                }
                return@setContent
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                //oyun alanı şeysi
                Column{
                    if(!state.connectedPlayers.contains("1")){
                        Text(
                            text = "1. oyuncu için bekleniyor",
                            color = Color.White
                        )
                    }
                    else if(!state.connectedPlayers.contains("2")){
                        Text(
                            text = "1. oyuncu için bekleniyor",
                            color =Color.White,
                        )
                    }
                    else{
                        Text(
                            text = "deeveye sormuşlar .. ",
                            color = Color.White
                        )
                    }
                }
            }
            if(isConnecting){
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                        contentAlignment = Alignment.Center
                ){
                    CircularProgressIndicator()
                }
            }

        }






    }


}