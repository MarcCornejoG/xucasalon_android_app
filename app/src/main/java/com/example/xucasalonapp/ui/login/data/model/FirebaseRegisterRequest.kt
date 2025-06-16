package com.example.xucasalonapp.ui.login.data.model

data class FirebaseRegisterRequest(
    val firebaseUid: String,
    val email: String,
    val nombre: String,
    val apellidos: String? = null,
    val photoUrl: String? = null
)