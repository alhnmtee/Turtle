package com.example.turtle

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.turtle.databinding.NormalGameRoomsBinding

class NormalGameRooms : Fragment(R.layout.normal_game_rooms) {
    private var _binding: NormalGameRoomsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = NormalGameRoomsBinding.bind(view)

        val wordSize = loadWordSize()
        val gameMode = loadGameMode()
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
    }
}