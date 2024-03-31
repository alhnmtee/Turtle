package com.example.turtle

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.turtle.R
import com.example.turtle.databinding.FragmentFirstBinding
import com.google.firebase.auth.FirebaseAuth
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.WebSockets

class FirstFragment : Fragment(R.layout.fragment_first) {
    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private val client = HttpClient {
        install(WebSockets)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonLogout.setOnClickListener {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.FirstFragment, true)
                .build()
            FirebaseAuth.getInstance().signOut()
            findNavController().navigate(R.id.action_FirstFragment_to_LoginFragment, null, navOptions)
        }
        binding.gameMode.setOnClickListener {
            saveGameMode("random")
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.FirstFragment, true)
                .build()
            findNavController().navigate(R.id.action_FirstFragment_to_RandomMode, null, navOptions)
        }
        binding.normalMode.setOnClickListener {
            saveGameMode("normal")
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.FirstFragment, true)
                .build()
            findNavController().navigate(R.id.action_FirstFragment_to_RandomMode, null, navOptions)
        }
    }

    private fun saveGameMode(mode: String) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString(getString(R.string.saved_game_mode), mode)
            apply()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}