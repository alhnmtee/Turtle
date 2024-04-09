package com.example.classes

import kotlinx.serialization.Serializable

@Serializable
data class RoomState(
    //noda kodu
    val connectedPlayers: List<String> = emptyList(),
    val playersCurrentlyPlaying :List<String> = emptyList(),
    val requests : Map<String , String> = emptyMap(),
    val rejectedPlayers : List<String> = emptyList(),




    //oyun
    val playerWon : String = "",
    val isGamePlaying :Boolean = false,



    //burası daha güzel yapılabilir
    /*


    val playerWords :Map<String , String> = emptyMap(),

    val playerGames : Map <String , Map<String , List<Int>>> = emptyMap(),

    */

    //bu mantıkta olabilir ama oyun sonuçta karşılılı 2 kişi tarafından oynanıyor .
    //Eğer oyunun kodu daha modüler ve mantıklı olucaksa yukarıdaki gibi yazılmalı ama yukarıdaki yapıyı güncellemesi filan zor


    val player1Id :String = "",
    val player2Id :String = "",

    val player1Word:String = "",
    val player2Word:String = "",

    val player1Game : Map<String,List<Int>> = emptyMap(),
    val player2Game : Map<String,List<Int>> = emptyMap(),


    val player1Score : Int = 0,
    val player2Score : Int = 0,


    ) {

}