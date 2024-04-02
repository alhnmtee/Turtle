package com.example.turtle

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.turtle.R

class GameRooms : Fragment(R.layout.game_rooms) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val wordSize = loadWordSize()
        // Use wordSize to filter game rooms and user list
        //Kullanılmıyor sanırım...
    }

    private fun loadWordSize(): Int {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return 0
        return sharedPref.getInt(getString(R.string.saved_word_size), 0)
    }
}