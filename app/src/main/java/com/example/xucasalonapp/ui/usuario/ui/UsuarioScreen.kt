package com.example.xucasalonapp.ui.usuario.ui

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.xucasalonapp.data.SessionManager
import com.example.xucasalonapp.data.api.ApiClient
import com.example.xucasalonapp.data.api.UsuarioService
import com.example.xucasalonapp.ui.login.data.model.AuthUser
import com.example.xucasalonapp.ui.usuario.data.UsuarioRepositoryImpl
import com.example.xucasalonapp.ui.usuario.ui.components.UserOptionButton

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UsuarioScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)

    val apiClient = ApiClient.getInstance(sessionManager)
    val usuarioService = apiClient.createService(UsuarioService::class.java)

    val repo = UsuarioRepositoryImpl(usuarioService, sessionManager)

    val viewModel: UsuarioViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                UsuarioViewModel(repo) as T
        }
    )

    val usuario by viewModel.usuario.collectAsState()
    LaunchedEffect(viewModel.logoutEvent) {
        viewModel.logoutEvent.collect {
            navController.navigate("login") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    usuario?.let { authUser ->
        val (nombre, apellidos, email, telefono, fechaRegistro) = when (authUser) {
            is AuthUser.TraditionalUser -> {
                val cliente = authUser.cliente
                UserData(
                    cliente.nombre,
                    cliente.apellidos,
                    cliente.email,
                    cliente.telefono,
                    cliente.fechaRegistro
                )
            }
            is AuthUser.FirebaseAuthUser -> {
                val firebaseUser = authUser.firebaseUser
                UserData(
                    firebaseUser.displayName ?: "Usuario",
                    "",
                    firebaseUser.email,
                    "",
                    "Registro con Google"
                )
            }
        }

        Scaffold { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Surface(
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (authUser is AuthUser.FirebaseAuthUser && !authUser.firebaseUser.photoUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = authUser.firebaseUser.photoUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = CircleShape
                                    )
                                    .padding(16.dp),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = nombre,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Información de contacto", style = MaterialTheme.typography.titleMedium)
                        Divider()
                        Text("Email: $email", style = MaterialTheme.typography.bodyMedium)
                        if (telefono.isNotEmpty()) {
                            Text("Teléfono: $telefono", style = MaterialTheme.typography.bodyMedium)
                        }
                        Text(
                            "Registrado: ${if (fechaRegistro.length > 10) fechaRegistro.take(10) else fechaRegistro}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    UserOptionButton(
                        icon = Icons.Default.List,
                        text = "Ver mis pedidos",
                        onClick = {
                            navController.navigate("pedidos")
                        }
                    )
                    UserOptionButton(
                        icon = Icons.Default.ExitToApp,
                        text = "Cerrar sesión",
                        onClick = { viewModel.cerrarSesion() }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    } ?: Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }

    BackHandler {
    }
}

data class UserData(
    val nombre: String,
    val apellidos: String,
    val email: String,
    val telefono: String,
    val fechaRegistro: String
)

