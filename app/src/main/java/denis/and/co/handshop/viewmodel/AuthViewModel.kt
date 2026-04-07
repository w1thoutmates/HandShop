package denis.and.co.handshop.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.*
import denis.and.co.handshop.data.repository.SellerRepository
import denis.and.co.handshop.ui.navigation.RecommendationRoute
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: SellerRepository
) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    var startDestination by mutableStateOf<Any?>(null)

    init {
        checkAuthState()
    }

    fun checkAuthState() {
        val user = auth.currentUser
        if (user == null) {
            startDestination = "login"
        } else {
            viewModelScope.launch {
                try {
                    val exists = repository.checkIfProfileExists(user.uid)
                    if (exists) {
                        startDestination = RecommendationRoute
                    } else {
                        startDestination = "create_profile"
                    }
                } catch (e: Exception) {
                    startDestination = "login"
                }
            }
        }
    }

    fun getAuth(): FirebaseAuth {
        return auth
    }
}