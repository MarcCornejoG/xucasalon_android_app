package com.example.xucasalonapp.ui.usuario.data


import android.util.Log
import com.example.xucasalonapp.core.Resource
import com.example.xucasalonapp.data.SessionManager
import com.example.xucasalonapp.data.api.UsuarioService
import com.example.xucasalonapp.data.model.LoginRequest
import com.example.xucasalonapp.data.model.LoginResponse
import com.example.xucasalonapp.ui.login.data.model.AuthUser
import com.example.xucasalonapp.ui.login.data.model.FirebaseRegisterRequest
import com.example.xucasalonapp.ui.login.data.model.FirebaseUser
import com.example.xucasalonapp.ui.login.data.model.LinkFirebaseRequest
import com.example.xucasalonapp.ui.usuario.data.model.Cliente
import com.example.xucasalonapp.ui.usuario.data.model.ClienteDTO

class UsuarioRepositoryImpl(
    private val service: UsuarioService,
    private val sessionManager: SessionManager
) : UsuarioRepository {

    override suspend fun login(email: String, password: String): Resource<Cliente> {
        return try {
            val resp = service.login(LoginRequest(email, password))
            if (resp.isSuccessful) {
                val body: LoginResponse = resp.body()!!
                sessionManager.saveToken(body.token)
                val cliente = Cliente(
                    idCliente     = body.idCliente,
                    email         = body.email ?: "",
                    nombre        = body.nombre ?: "",
                    apellidos     = body.apellidos ?: "",
                    telefono      = body.telefono ?: "",
                    direccion     = body.direccion,
                    fechaRegistro = body.fechaRegistro ?: ""
                )
                sessionManager.saveUser(cliente)
                Resource.Success(cliente)
            } else {
                Resource.Error("Error ${resp.code()}: ${resp.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error de red")
        }
    }

    override suspend fun register(clienteDTO: ClienteDTO): Resource<Cliente> {
        return try {
            val resp = service.register(clienteDTO)
            if (resp.isSuccessful) {
                val body = resp.body()!!
                val cliente = Cliente(
                    idCliente = body.idCliente ?: 0,
                    email = body.email ?: "",
                    nombre = body.nombre ?: "",
                    apellidos = body.apellidos ?: "",
                    telefono = body.telefono ?: "",
                    direccion = body.direccion,
                    fechaRegistro = body.fechaRegistro?.toString() ?: ""
                )
                Resource.Success(cliente)
            } else {
                Resource.Error("Error ${resp.code()}: ${resp.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error de red")
        }
    }

    override fun getUsuarioActual(): AuthUser? =
        sessionManager.getCurrentAuthUser()

    override fun cerrarSesion() {
        sessionManager.clearSession()
    }

    override suspend fun registerOrLinkFirebaseUser(firebaseUser: FirebaseUser): Resource<Cliente> {
        Log.d("Repository", "=== registerOrLinkFirebaseUser INICIADO ===")
        return try {
            val registerRequest = FirebaseRegisterRequest(
                firebaseUid = firebaseUser.firebaseUid,
                email = firebaseUser.email,
                nombre = firebaseUser.displayName,
                apellidos = extractApellidos(firebaseUser.displayName),
                photoUrl = firebaseUser.photoUrl
            )

            Log.d("Repository", "Intentando registrar nuevo usuario...")
            Log.d("Repository", "RegisterRequest: $registerRequest")

            val resp = service.registerFirebaseUser(registerRequest)

            Log.d("Repository", "Response code: ${resp.code()}")
            Log.d("Repository", "Response successful: ${resp.isSuccessful}")

            if (resp.isSuccessful) {
                val body = resp.body()!!
                Log.d("Repository", "Usuario nuevo registrado: $body")

                val cliente = Cliente(
                    idCliente = body.idCliente ?: 0,
                    email = body.email ?: "",
                    nombre = body.nombre ?: "",
                    apellidos = body.apellidos ?: "",
                    telefono = body.telefono ?: "",
                    direccion = body.direccion,
                    fechaRegistro = body.fechaRegistro ?: "",
                    firebaseUid = firebaseUser.firebaseUid
                )

                sessionManager.saveUser(cliente)
                return Resource.Success(cliente)

            } else if (resp.code() == 409) {
                Log.d("Repository", "Usuario ya existe (409), intentando vincular...")
                return linkExistingAccount(firebaseUser)

            } else {
                val errorBody = resp.errorBody()?.string()
                Log.e("Repository", "Error ${resp.code()}: $errorBody")
                return Resource.Error("Error ${resp.code()}: $errorBody")
            }

        } catch (e: Exception) {
            Log.e("Repository", "Exception en registerOrLinkFirebaseUser: ${e.message}", e)
            return Resource.Error(e.message ?: "Error de red")
        }
    }

    private suspend fun linkExistingAccount(firebaseUser: FirebaseUser): Resource<Cliente> {
        Log.d("Repository", "=== linkExistingAccount INICIADO ===")
        return try {
            val linkRequest = LinkFirebaseRequest(
                firebaseUid = firebaseUser.firebaseUid,
                email = firebaseUser.email
            )

            Log.d("Repository", "LinkRequest: $linkRequest")

            val resp = service.linkFirebaseAccount(linkRequest)

            Log.d("Repository", "Link response code: ${resp.code()}")
            Log.d("Repository", "Link response successful: ${resp.isSuccessful}")

            if (resp.isSuccessful) {
                val body = resp.body()!!
                Log.d("Repository", "Cuenta vinculada exitosamente: $body")

                val cliente = Cliente(
                    idCliente = body.idCliente ?: 0,
                    email = body.email ?: "",
                    nombre = body.nombre ?: "",
                    apellidos = body.apellidos ?: "",
                    telefono = body.telefono ?: "",
                    direccion = body.direccion,
                    fechaRegistro = body.fechaRegistro ?: "",
                    firebaseUid = firebaseUser.firebaseUid
                )

                sessionManager.saveUser(cliente)
                return Resource.Success(cliente)

            } else {
                val errorBody = resp.errorBody()?.string()
                Log.e("Repository", "Error al vincular: ${resp.code()}: $errorBody")
                return Resource.Error("Error al vincular cuentas: $errorBody")
            }

        } catch (e: Exception) {
            Log.e("Repository", "Exception en linkExistingAccount: ${e.message}", e)
            return Resource.Error(e.message ?: "Error al vincular cuentas")
        }
    }

    private fun extractApellidos(displayName: String): String {
        val parts = displayName.trim().split(" ")
        return if (parts.size > 1) {
            parts.drop(1).joinToString(" ")
        } else {
            ""
        }
    }
}
