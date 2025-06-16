package com.example.xucasalonapp.ui.login.data

data class RegisterData(
    val email: String = "",
    val password: String = "",
    val nombre: String = "",
    val apellidos: String = "",
    val telefono: String = "",
    val direccion: String = ""
)
data class RegisterUiState(
    val registerData: RegisterData = RegisterData(),
    val enableButton: Boolean = false,
    val message: String = ""
)