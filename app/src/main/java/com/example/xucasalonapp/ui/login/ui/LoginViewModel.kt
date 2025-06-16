package com.example.xucasalonapp.ui.login.ui


import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.xucasalonapp.core.Resource
import com.example.xucasalonapp.data.SessionManager
import com.example.xucasalonapp.ui.login.data.LoginData
import com.example.xucasalonapp.ui.login.data.model.AuthUser
import com.example.xucasalonapp.ui.login.data.model.FirebaseUser
import com.example.xucasalonapp.ui.usuario.data.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: UsuarioRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _loginState = MutableStateFlow<Resource<AuthUser>>(Resource.Loading)
    val loginState: StateFlow<Resource<AuthUser>> = _loginState.asStateFlow()

    init {
        checkExistingSession()
    }

    private fun checkExistingSession() {
        viewModelScope.launch {
            when {
                sessionManager.hasCompleteUserProfile() -> {
                    val currentUser = sessionManager.getCurrentAuthUser()
                    _loginState.value = Resource.Success(currentUser!!)
                    Log.d("LoginViewModel", "Sesión existente encontrada: usuario completo")
                }

                sessionManager.isGoogleUser() && sessionManager.needsProfileCompletion() -> {
                    val firebaseUser = sessionManager.getGoogleUser()!!

                    when (val result = repository.registerOrLinkFirebaseUser(firebaseUser)) {
                        is Resource.Success -> {
                            sessionManager.saveHybridUser(result.data, firebaseUser)
                            _loginState.value = Resource.Success(AuthUser.TraditionalUser(result.data))
                            Log.d("LoginViewModel", "Perfil completado automáticamente")
                        }
                        is Resource.Error -> {
                            _loginState.value = Resource.Success(AuthUser.FirebaseAuthUser(firebaseUser))
                            Log.d("LoginViewModel", "Usuario Firebase sin perfil completo")
                        }
                        else -> {
                            _loginState.value = Resource.Loading
                        }
                    }
                }

                else -> {
                    _loginState.value = Resource.Loading
                    Log.d("LoginViewModel", "No hay sesión existente")
                }
            }
        }
    }

    fun onLoginChanged(email: String, password: String) {
        val validEmail = isValidEmail(email)
        val validPassword = isValidPassword(password)
        _uiState.update {
            it.copy(
                loginData = LoginData(email, password),
                loginEnable = validEmail && validPassword,
                loginMessage = ""
            )
        }
    }

    fun onTraditionalLoginSelected() {
        if (!_uiState.value.loginEnable) {
            _uiState.update { it.copy(loginMessage = "Revisa tus credenciales") }
            return
        }

        viewModelScope.launch {
            _loginState.value = Resource.Loading

            when (val result = repository.login(_uiState.value.loginData.email, _uiState.value.loginData.password)) {
                is Resource.Success -> {
                    _loginState.value = Resource.Success(AuthUser.TraditionalUser(result.data))
                }
                is Resource.Error -> {
                    _loginState.value = Resource.Error(result.message)
                }
                else -> {}
            }
        }
    }

    fun onFirebaseLoginSuccess(
        email: String,
        name: String,
        photoUrl: String? = null,
        firebaseUid: String
    ) {
        Log.d("LoginViewModel", "=== onFirebaseLoginSuccess INICIADO ===")
        Log.d("LoginViewModel", "Email: $email, Name: $name, FirebaseUid: $firebaseUid")

        viewModelScope.launch {
            _loginState.value = Resource.Loading

            val firebaseUser = FirebaseUser(
                firebaseUid = firebaseUid,
                email = email,
                displayName = name,
                photoUrl = photoUrl
            )

            try {
                sessionManager.saveGoogleUser(firebaseUser)
                Log.d("LoginViewModel", "Usuario Firebase guardado temporalmente")

                when (val result = repository.registerOrLinkFirebaseUser(firebaseUser)) {
                    is Resource.Success -> {
                        sessionManager.saveHybridUser(result.data, firebaseUser)

                        val authUser = AuthUser.TraditionalUser(result.data)
                        _loginState.value = Resource.Success(authUser)

                        Log.d("LoginViewModel", "Usuario híbrido creado exitosamente: ${result.data}")
                    }
                    is Resource.Error -> {
                        Log.e("LoginViewModel", "Error al crear/vincular cliente: ${result.message}")

                        val authUser = AuthUser.FirebaseAuthUser(firebaseUser)
                        _loginState.value = Resource.Success(authUser)
                    }
                    else -> {}
                }

            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error en login de Firebase: ${e.message}", e)
                _loginState.value = Resource.Error("Error en login: ${e.message}")
            }
        }
    }


    fun onLoginStateHandled() {
        Log.d("LoginViewModel", "=== onLoginStateHandled EJECUTADO ===")
        Log.d("LoginViewModel", "Estado actual antes de cambiar: ${_loginState.value}")
        _loginState.value = Resource.Loading
        Log.d("LoginViewModel", "Estado cambiado a Loading")
    }
    fun logout() {
        viewModelScope.launch {
            repository.cerrarSesion()
            _loginState.value = Resource.Loading
        }
    }

    // Método para obtener el usuario actual
    fun getCurrentUser(): AuthUser? {
        return sessionManager.getCurrentAuthUser()
    }

    private fun isValidPassword(password: String) = password.length > 2
    private fun isValidEmail(email: String) = Patterns.EMAIL_ADDRESS.matcher(email).matches()






}

class LoginViewModelFactory(
    private val repository: UsuarioRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}