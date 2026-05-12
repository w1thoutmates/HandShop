package denis.and.co.handshop.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.Firebase
import com.google.firebase.auth.*
import denis.and.co.handshop.data.repository.SellerRepository
import denis.and.co.handshop.ui.navigation.CreateProfileRoute
import denis.and.co.handshop.ui.navigation.LoginRoute
import denis.and.co.handshop.ui.navigation.RecommendationRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: SellerRepository
) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    var startDestination by mutableStateOf<Any?>(null)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    sealed class AuthState {
        object Loading : AuthState()
        object Unauthenticated : AuthState()
        object AuthenticatedWithoutProfile : AuthState()
        object AuthenticatedWithProfile : AuthState()
    }

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                _authState.value = AuthState.Unauthenticated
                startDestination = LoginRoute
            } else {
                checkProfileExists(user.uid)
            }
        }
    }

    fun checkAuthState() {
        val user = auth.currentUser
        if (user == null) {
            startDestination = LoginRoute
        } else {
            viewModelScope.launch {
                try {
                    val exists = repository.checkIfProfileExists(user.uid)
                    if (exists) {
                        startDestination = RecommendationRoute
                    } else {
                        startDestination = CreateProfileRoute
                    }
                } catch (e: Exception) {
                    startDestination = LoginRoute
                }
            }
        }
    }

    private fun checkProfileExists(uid: String) {
        viewModelScope.launch {
            try {
                val exists = repository.checkIfProfileExists(uid)
                if (exists) {
                    _authState.value = AuthState.AuthenticatedWithProfile
                    startDestination = RecommendationRoute
                } else {
                    _authState.value = AuthState.AuthenticatedWithoutProfile
                    startDestination = CreateProfileRoute
                }
            } catch (ex: Exception) {
                _authState.value = AuthState.Unauthenticated
                startDestination = LoginRoute
            }
        }
    }

    fun getAuth(): FirebaseAuth {
        return auth
    }

    fun signOut(context: Context, onComplete: () -> Unit) {
        auth.signOut()
        val oneTapClient = Identity.getSignInClient(context)
        oneTapClient.signOut().addOnCompleteListener {
            _authState.value = AuthState.Unauthenticated
            onComplete()
        }
    }
}