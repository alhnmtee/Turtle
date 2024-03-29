package com.example.turtle

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.turtle.databinding.RandomModeBinding

class RandomMode : Fragment(R.layout.random_mode) {
    private var _binding: RandomModeBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = RandomModeBinding.bind(view)

        val gameMode = getGameMode()
        binding.gameModeTextView.text = "Selected game mode: $gameMode"

        binding.button4Letters.setOnClickListener {
            saveGameSettings(4)
        }

        binding.button5Letters.setOnClickListener {
            saveGameSettings(5)
        }

        binding.button6Letters.setOnClickListener {
            saveGameSettings(6)
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_randomModeFragment_to_FirstFragment)
        }
    }

    private fun getGameMode(): String? {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        return sharedPref?.getString(getString(R.string.saved_game_mode), "")
    }

    private fun saveGameSettings(size: Int) {
        val gameMode = getGameMode()
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putInt(getString(R.string.saved_word_size), size)
            apply()
        }
        if (gameMode == "random") {
            navigateToRandomGameRooms()
        } else {
            navigateToNormalGameRooms()
        }
    }

    private fun navigateToRandomGameRooms() {
        findNavController().navigate(R.id.action_randomModeFragment_to_randomGameRoomsFragment)
    }

    private fun navigateToNormalGameRooms() {
        findNavController().navigate(R.id.action_randomModeFragment_to_normalGameRoomsFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}