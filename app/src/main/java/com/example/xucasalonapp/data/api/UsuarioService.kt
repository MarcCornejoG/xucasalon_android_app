package com.example.xucasalonapp.data.api

import com.example.xucasalonapp.data.model.LoginRequest
import com.example.xucasalonapp.data.model.LoginResponse
import com.example.xucasalonapp.ui.login.data.model.FirebaseRegisterRequest
import com.example.xucasalonapp.ui.login.data.model.LinkFirebaseRequest
import com.example.xucasalonapp.ui.usuario.data.model.ClienteDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UsuarioService {
    @POST("auth/login")
    suspend fun login(@Body req: LoginRequest): Response<LoginResponse>

    @POST("/api/clientes/add")
    suspend fun register(@Body clienteDTO: ClienteDTO): Response<ClienteDTO>

    @POST("auth/register-firebase")
    suspend fun registerFirebaseUser(@Body request: FirebaseRegisterRequest): Response<ClienteDTO>

    @POST("auth/link-firebase")
    suspend fun linkFirebaseAccount(@Body request: LinkFirebaseRequest): Response<ClienteDTO>
}