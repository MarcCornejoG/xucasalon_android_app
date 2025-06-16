package com.example.xucasalonapp.ui.login.ui

import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.Error
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import kotlinx.coroutines.delay
import com.example.xucasalonapp.R
import com.example.xucasalonapp.core.Resource
import com.example.xucasalonapp.data.SessionManager
import com.example.xucasalonapp.data.api.ApiClient
import com.example.xucasalonapp.data.api.UsuarioService
import com.example.xucasalonapp.ui.login.data.model.AuthUser
import com.example.xucasalonapp.ui.usuario.data.UsuarioRepositoryImpl
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: (AuthUser) -> Unit = {},
    onNavigateToRegister: () -> Unit
) {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    val sessionManager = SessionManager(context)
    val apiClient = ApiClient.getInstance(sessionManager)
    val usuarioService = apiClient.createService(UsuarioService::class.java)
    val repo = UsuarioRepositoryImpl(usuarioService, sessionManager)

    val factory = LoginViewModelFactory(repo, sessionManager)
    val loginViewModel: LoginViewModel = viewModel(factory = factory)

    val uiState by loginViewModel.uiState.collectAsState()
    val loginState by loginViewModel.loginState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var showCredentialsError by remember { mutableStateOf(false) }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("GoogleSignIn", "=== LAUNCHER EJECUTADO ===")
        Log.d("GoogleSignIn", "Result code: ${result.resultCode}")
        Log.d("GoogleSignIn", "RESULT_OK: ${Activity.RESULT_OK}")
        Log.d("GoogleSignIn", "Data: ${result.data}")

        if (result.resultCode == Activity.RESULT_OK) {
            Log.d("GoogleSignIn", "Procesando resultado exitoso...")
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d("GoogleSignIn", "Cuenta obtenida:")
                Log.d("GoogleSignIn", "- Email: ${account.email}")
                Log.d("GoogleSignIn", "- Name: ${account.displayName}")
                Log.d("GoogleSignIn", "- ID: ${account.id}")
                Log.d("GoogleSignIn", "- PhotoUrl: ${account.photoUrl}")

                loginViewModel.onFirebaseLoginSuccess(
                    email = account.email ?: "",
                    name = account.displayName ?: "Usuario",
                    photoUrl = account.photoUrl?.toString(),
                    firebaseUid = account.id ?: ""
                )
                Log.d("GoogleSignIn", "onFirebaseLoginSuccess llamado exitosamente")
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Error al obtener cuenta: ${e.statusCode} - ${e.message}")
                Toast.makeText(context, "Error en Google Sign-In: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            Log.d("GoogleSignIn", "Login cancelado o falló - Result code: ${result.resultCode}")
        }
    }

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestProfile()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    when (val currentLoginState = loginState) {
        is Resource.Loading -> {
            Log.d("LoginScreen", "Estado: LOADING")
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is Resource.Success -> {
            Log.d("LoginScreen", "=== ESTADO SUCCESS DETECTADO ===")
            Log.d("LoginScreen", "Datos recibidos: ${currentLoginState.data}")
            LaunchedEffect(currentLoginState) {
                Log.d("LoginScreen", "LaunchedEffect ejecutándose...")
                onLoginSuccess(currentLoginState.data)
                Log.d("LoginScreen", "onLoginSuccess ejecutado")
                loginViewModel.onLoginStateHandled()
                Log.d("LoginScreen", "onLoginStateHandled ejecutado")
            }
        }
        is Resource.Error -> {
            Log.e("LoginScreen", "Estado ERROR: ${currentLoginState.message}")
            LaunchedEffect(currentLoginState) {
                val isCredentialsError = currentLoginState.message.contains("credenciales", ignoreCase = true) ||
                        currentLoginState.message.contains("incorrect", ignoreCase = true) ||
                        currentLoginState.message.contains("invalid", ignoreCase = true) ||
                        currentLoginState.message.contains("unauthorized", ignoreCase = true) ||
                        currentLoginState.message.contains("401")

                if (isCredentialsError) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)

                    Toast.makeText(
                        context,
                        "🔐 Credenciales incorrectas\nVerifica tu email y contraseña",
                        Toast.LENGTH_LONG
                    ).show()

                    showCredentialsError = true
                    delay(3000)
                    showCredentialsError = false
                } else {
                    Toast.makeText(context, currentLoginState.message, Toast.LENGTH_LONG).show()
                }

                loginViewModel.onLoginStateHandled()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFEDE0C8),
                        Color(0xFFFFFFFF)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Card(
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 32.dp),
                shape = RoundedCornerShape(60.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.logo_xucasalon_anteproyecto),
                        contentDescription = "Logo Xuca Salón",
                        modifier = Modifier.size(80.dp)
                    )
                }
            }

            Text(
                text = "Xuca Salón",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Bienvenido",
                fontSize = 16.sp,
                color = Color.Black.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 48.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Iniciar Sesión",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    if (showCredentialsError) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFEBEE)
                            ),
                            border = BorderStroke(1.dp, Color(0xFFE57373))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = "Error",
                                    tint = Color(0xFFD32F2F),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Credenciales incorrectas. Verifica tus datos.",
                                    color = Color(0xFFD32F2F),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = uiState.loginData.email,
                        onValueChange = { email ->
                            loginViewModel.onLoginChanged(email, uiState.loginData.password)
                            if (showCredentialsError) {
                                showCredentialsError = false
                            }
                        },
                        label = { Text("Correo electrónico") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email"
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (showCredentialsError) Color(0xFFE57373) else Color(0xFF6A5ACD),
                            focusedLabelColor = if (showCredentialsError) Color(0xFFE57373) else Color(0xFF6A5ACD),
                            focusedLeadingIconColor = if (showCredentialsError) Color(0xFFE57373) else Color(0xFF6A5ACD),
                            unfocusedBorderColor = if (showCredentialsError) Color(0xFFE57373) else Color.Gray
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = uiState.loginData.password,
                        onValueChange = { password ->
                            loginViewModel.onLoginChanged(uiState.loginData.email, password)
                            if (showCredentialsError) {
                                showCredentialsError = false
                            }
                        },
                        label = { Text("Contraseña") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Password"
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (showCredentialsError) Color(0xFFE57373) else Color(0xFF6A5ACD),
                            focusedLabelColor = if (showCredentialsError) Color(0xFFE57373) else Color(0xFF6A5ACD),
                            focusedLeadingIconColor = if (showCredentialsError) Color(0xFFE57373) else Color(0xFF6A5ACD),
                            unfocusedBorderColor = if (showCredentialsError) Color(0xFFE57373) else Color.Gray
                        ),
                        singleLine = true
                    )

                    Button(
                        onClick = { loginViewModel.onTraditionalLoginSelected() },
                        enabled = uiState.loginEnable,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6A5ACD),
                            disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(
                            text = "Iniciar Sesión",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(
                            modifier = Modifier.weight(1f),
                            color = Color.Gray.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "  O  ",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Divider(
                            modifier = Modifier.weight(1f),
                            color = Color.Gray.copy(alpha = 0.5f)
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            Log.d("GoogleSignIn", "=== BOTÓN GOOGLE PRESIONADO ===")
                            googleSignInClient.signOut().addOnCompleteListener {
                                Log.d("GoogleSignIn", "Sign out completado, iniciando fresh sign in")
                                val signInIntent = googleSignInClient.signInIntent
                                Log.d("GoogleSignIn", "Intent creado: $signInIntent")
                                googleSignInLauncher.launch(signInIntent)
                                Log.d("GoogleSignIn", "Launcher ejecutado")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.google_logo),
                                contentDescription = "Google",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Continuar con Google",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = { onNavigateToRegister() }
                    ) {
                        Text(
                            text = "¿No tienes cuenta? Regístrate aquí",
                            color = Color(0xFF6A5ACD),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

        }
    }
}