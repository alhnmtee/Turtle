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
import io.ktor.client.features.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.http.URLProtocol
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText
import io.ktor.http.cio.websocket.send
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirstFragment : Fragment() {
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
        binding.buttonFirst.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    client.webSocket(
                        method = HttpMethod.Get,
                        host = "10.0.2.2",
                        port = 8080,
                        path = "/room"
                    ) {
                        println("Connected to server")
                    }
                } catch (e: Exception) {
                    // Bağlantı hatası durumunda yapılacak işlemler
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
