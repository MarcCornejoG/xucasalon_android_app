package com.example.xucasalonapp.ui.login.data.model

import com.example.xucasalonapp.ui.usuario.data.model.Cliente

data class FirebaseUser(
    val firebaseUid: String,
    val email: String,
    val displayName: String,
    val photoUrl: String?
)
sealed class AuthUser {
    data class TraditionalUser(val cliente: Cliente) : AuthUser()
    data class FirebaseAuthUser(val firebaseUser: FirebaseUser) : AuthUser()
}