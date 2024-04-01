package com.example.turtle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.turtle.databinding.FragmentRegisterBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.password.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val password = binding.password.text.toString()
                if (password.length < 8) {
                    binding.password.error = "Password must be at least 8 characters"
                }
            } else {
                binding.password.error = null
            }
        }
        binding.register.setOnClickListener {
            val email = binding.username.text.toString()
            val password = binding.password.text.toString()

            registerUser(email, password)

        }
    }
    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Snackbar.make(binding.root, "Registration successful!", Snackbar.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_registerFragment_to_LoginFragment)
                } else {
                    val errorMessage = task.exception?.message ?: "Registration failed!"
                    Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
                }
            }
    }
}