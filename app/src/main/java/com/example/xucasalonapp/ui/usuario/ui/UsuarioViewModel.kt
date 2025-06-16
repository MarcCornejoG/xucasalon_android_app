package com.example.xucasalonapp.ui.usuario.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xucasalonapp.ui.login.data.model.AuthUser
import com.example.xucasalonapp.ui.usuario.data.UsuarioRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsuarioViewModel(
    private val repository: UsuarioRepository
) : ViewModel() {

    private val _usuario = MutableStateFlow<AuthUser?>(null)
    val usuario: StateFlow<AuthUser?> = _usuario

    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent: SharedFlow<Unit> = _logoutEvent

    init {
        cargarUsuario()
    }

    private fun cargarUsuario() {
        _usuario.value = repository.getUsuarioActual()
    }

    fun cerrarSesion() {
        repository.cerrarSesion()
        viewModelScope.launch {
            _logoutEvent.emit(Unit)
        }
    }
}