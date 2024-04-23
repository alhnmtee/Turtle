package com.example.turtle

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.turtle.databinding.FragmentFirstBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets

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

    private fun makePopUp(context : Context,it : View){
        val fireStoreCollectionReference = FirebaseFirestore.getInstance().collection("Usernames")
        val popUp = LayoutInflater.from(this.context).inflate(R.layout.username_popup,null)

        val popupWindow = PopupWindow(
            popUp,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.elevation = 10f
        popupWindow.showAtLocation(it, Gravity.CENTER, 0, 0)




        popUp.findViewById<Button>(R.id.username_cancel_button).setOnClickListener {
            popupWindow.dismiss()
        }

        popUp.findViewById<Button>(R.id.username_confirm_button).setOnClickListener {
            val text = popUp.findViewById<EditText>(R.id.username_edittext).text
            FirebaseAuth.getInstance().uid?.let { it1 -> fireStoreCollectionReference.document(it1).set(mapOf("username" to text.toString().trim())
            )}
            popupWindow.dismiss()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //bunu kullanmasak da bağlantı erkenden oluşsun diye yazdım!
        val fireStoreCollectionReference = FirebaseFirestore.getInstance().collection("Usernames")

        binding.buttonLogout.setOnClickListener {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.FirstFragment, true)
                .build()
            FirebaseAuth.getInstance().signOut()
            findNavController().navigate(R.id.action_FirstFragment_to_LoginFragment, null, navOptions)
        }

       binding.buttonChangeUsername.setOnClickListener{
           this.context?.let { it1 -> makePopUp(it1,it) }
       }


        binding.buttonRandomMode.setOnClickListener {
            saveGameMode("random")
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.FirstFragment, true)
                .build()
            findNavController().navigate(R.id.action_FirstFragment_to_RandomMode, null, navOptions)
        }
        binding.buttonUnrestrictedMode.setOnClickListener {
            saveGameMode("normal")
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.FirstFragment, true)
                .build()
            findNavController().navigate(R.id.action_FirstFragment_to_RandomMode, null, navOptions)
        }
        binding.buttonRandomLetterMode.setOnClickListener {
            saveGameMode("letter")
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