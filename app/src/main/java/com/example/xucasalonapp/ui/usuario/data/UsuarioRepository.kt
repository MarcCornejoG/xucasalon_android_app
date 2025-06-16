package com.example.xucasalonapp.ui.usuario.data

import com.example.xucasalonapp.core.Resource
import com.example.xucasalonapp.ui.login.data.model.AuthUser
import com.example.xucasalonapp.ui.login.data.model.FirebaseUser
import com.example.xucasalonapp.ui.usuario.data.model.Cliente
import com.example.xucasalonapp.ui.usuario.data.model.ClienteDTO

interface UsuarioRepository{

    suspend fun login(email: String, password: String): Resource<Cliente>
    suspend fun register(clienteDTO: ClienteDTO): Resource<Cliente>
    fun getUsuarioActual(): AuthUser?

    fun cerrarSesion()

    suspend fun registerOrLinkFirebaseUser(firebaseUser: FirebaseUser): Resource<Cliente>
}