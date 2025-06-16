package com.example.xucasalonapp

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.xucasalonapp.data.SessionManager
import com.example.xucasalonapp.data.api.ApiClient
import com.example.xucasalonapp.ui.citas.data.model.Cita
import com.example.xucasalonapp.ui.citas.ui.CitasScreen
import com.example.xucasalonapp.ui.citas.ui.ConsultarCitasScreen
import com.example.xucasalonapp.ui.citas.ui.SolicitarCitaScreen
import com.example.xucasalonapp.ui.login.ui.LoginScreen
import com.example.xucasalonapp.ui.login.ui.RegisterScreen
import com.example.xucasalonapp.ui.mainscreen.ui.MainScreen
import com.example.xucasalonapp.ui.navigation.BottomNavItem
import com.example.xucasalonapp.ui.navigation.BottomNavigationBar
import com.example.xucasalonapp.ui.pedidos.data.PedidoRepositoryImpl
import com.example.xucasalonapp.ui.pedidos.data.PedidoService
import com.example.xucasalonapp.ui.pedidos.ui.CarritoScreen
import com.example.xucasalonapp.ui.pedidos.ui.CarritoViewModel
import com.example.xucasalonapp.ui.pedidos.ui.CarritoViewModelFactory
import com.example.xucasalonapp.ui.pedidos.ui.PedidosScreen
import com.example.xucasalonapp.ui.productos.ui.ProductDetailScreen
import com.example.xucasalonapp.ui.productos.ui.ProductosScreen
import com.example.xucasalonapp.ui.theme.XucaSalonAppTheme
import com.example.xucasalonapp.ui.usuario.ui.UsuarioScreen
import com.stripe.android.PaymentConfiguration

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PaymentConfiguration.init(

            applicationContext,
            "Testing for Stripe"
        )
        enableEdgeToEdge()

        setContent {
            XucaSalonAppTheme {
                val navController = rememberNavController()

                val context = LocalContext.current.applicationContext
                val sessionManager = remember { SessionManager(context) }

                val apiClient = ApiClient.getInstance(sessionManager)
                val pedidoService = apiClient.createService(PedidoService::class.java)
                val pedidoRepository = PedidoRepositoryImpl(pedidoService)

                val carritoViewModel: CarritoViewModel = viewModel(
                    factory = CarritoViewModelFactory(sessionManager, pedidoRepository)
                )


                var citaParaEditar by remember { mutableStateOf<Cita?>(null) }

                val bottomRoutes = listOf(
                    BottomNavItem.Home.route,
                    BottomNavItem.Citas.route,
                    BottomNavItem.Productos.route,
                    BottomNavItem.Usuario.route
                )

                val backStack by navController.currentBackStackEntryAsState()
                val currentRoute = backStack?.destination?.route

                Scaffold(
                    bottomBar = {
                        if (currentRoute in bottomRoutes) {
                            BottomNavigationBar(navController)
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "login",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("login") {
                            Log.d("MainActivity", "Composable login creado")
                            LoginScreen(
                                onLoginSuccess = { authUser ->
                                    Log.d("MainActivity", "=== onLoginSuccess EJECUTADO ===")
                                    Log.d("MainActivity", "AuthUser recibido: $authUser")
                                    Log.d("MainActivity", "Navegando a home...")

                                    navController.navigate(BottomNavItem.Home.route) {
                                        popUpTo("login") { inclusive = true }
                                    }
                                    Log.d("MainActivity", "Navegación completada")
                                },
                                onNavigateToRegister = {
                                    Log.d("MainActivity", "Navegando a register")
                                    navController.navigate("register")
                                }
                            )
                        }

                        navigation(
                            route = "root",
                            startDestination = BottomNavItem.Home.route
                        ) {
                            composable(BottomNavItem.Home.route) {
                                MainScreen(navController)
                            }
                            composable(BottomNavItem.Citas.route) {
                                CitasScreen(navController)
                            }
                            composable(BottomNavItem.Productos.route) {
                                ProductosScreen(
                                    navController = navController,
                                    sessionManager = sessionManager,
                                    carritoViewModel = carritoViewModel
                                )
                            }
                            composable(BottomNavItem.Usuario.route) {
                                UsuarioScreen(navController)
                            }
                            composable("consultar_citas") {
                                ConsultarCitasScreen(
                                    onNavigateBack = {
                                        navController.popBackStack()
                                    },
                                    onSolicitarCita = {
                                        citaParaEditar = null
                                        navController.navigate("solicitar_cita")
                                    },
                                    onModificarCita = { cita ->
                                        citaParaEditar = cita
                                        navController.navigate("solicitar_cita")
                                    },
                                    sessionManager = sessionManager
                                )
                            }
                            composable("solicitar_cita") {
                                SolicitarCitaScreen(
                                    onNavigateBack = {
                                        citaParaEditar = null
                                        navController.popBackStack()
                                    },
                                    onCitaCreada = {
                                        citaParaEditar = null
                                        navController.popBackStack()
                                    },
                                    sessionManager = sessionManager,
                                    citaParaEditar = citaParaEditar
                                )
                            }



                            composable(
                                route = "producto/{id}",
                                arguments = listOf(navArgument("id") { type = NavType.IntType })
                            ) { backStackEntry ->
                                val id = backStackEntry.arguments!!.getInt("id")

                                ProductDetailScreen(
                                    productId = id,
                                    navController = navController,
                                    sessionManager = sessionManager,
                                    carritoViewModel = carritoViewModel
                                )
                            }

                            composable("pedidos") {
                                PedidosScreen(navController)
                            }

                            composable("carrito") {
                                CarritoScreen(
                                    navController = navController,
                                    sessionManager = sessionManager,
                                    carritoViewModel = carritoViewModel
                                )
                            }
                            composable("register") {
                                RegisterScreen(
                                    navController = navController,
                                    onRegisterSuccess = {
                                        navController.popBackStack()
                                    })
                            }
                        }
                    }
                }
            }
        }
    }
}