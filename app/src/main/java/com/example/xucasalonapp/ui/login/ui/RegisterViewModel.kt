package com.example.xucasalonapp.ui.login.ui

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xucasalonapp.core.Resource
import com.example.xucasalonapp.ui.login.data.RegisterData
import com.example.xucasalonapp.ui.login.data.RegisterUiState
import com.example.xucasalonapp.ui.usuario.data.UsuarioRepository
import com.example.xucasalonapp.ui.usuario.data.model.Cliente
import com.example.xucasalonapp.ui.usuario.data.model.ClienteDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _registerState = MutableStateFlow<Resource<Cliente>>(Resource.Loading)
    val registerState: StateFlow<Resource<Cliente>> = _registerState.asStateFlow()

    fun onRegisterChanged(
        email: String, password: String,
        nombre: String, apellidos: String,
        telefono: String, direccion: String
    ) {
        val validEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val validPassword = password.length > 2
        val fieldsFilled = listOf(nombre, apellidos, telefono).all { it.isNotBlank() }
        _uiState.update {
            it.copy(
                registerData = RegisterData(email, password, nombre, apellidos, telefono, direccion),
                enableButton = validEmail && validPassword && fieldsFilled,
                message = ""
            )
        }
    }

    fun onRegisterSelected() {
        if (!_uiState.value.enableButton) {
            _uiState.update { it.copy(message = "Rellena todos los campos correctamente") }
            return
        }
        viewModelScope.launch {
            _registerState.value = Resource.Loading
            val data = _uiState.value.registerData
            val dto = ClienteDTO(
                idCliente = null,
                email     = data.email,
                nombre    = data.nombre,
                apellidos = data.apellidos,
                telefono  = data.telefono,
                direccion = data.direccion,
                password  = data.password,
                fechaRegistro = null
            )
            _registerState.value = repository.register(dto)
        }
    }

    fun onRegisterHandled() {
        _registerState.value = Resource.Loading
    }
}