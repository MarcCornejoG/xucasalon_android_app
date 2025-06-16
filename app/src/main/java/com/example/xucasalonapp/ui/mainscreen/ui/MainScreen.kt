package com.example.xucasalonapp.ui.mainscreen.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.xucasalonapp.R

import com.example.xucasalonapp.ui.mainscreen.ui.components.SalonDescription
import com.example.xucasalonapp.ui.mainscreen.ui.components.SalonMap
import com.example.xucasalonapp.ui.navigation.BottomNavItem

@Composable
fun MainScreen(
    navController: NavController
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF3E0), Color.White)
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.logo_xucasalon_anteproyecto),
            contentDescription = "Logo Xuca Salón",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Gray, CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp))

        SalonDescription()

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Encuéntranos aquí:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        SalonMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.height(24.dp))

        MainOptionButton(
            icon = Icons.Default.CalendarMonth,
            label = "Reservar Cita",
            onClick = {navController.navigate(BottomNavItem.Citas.route)}

        ) 

        MainOptionButton(
            icon = Icons.Default.ShoppingBag,
            label = "Ver Productos",
            onClick = {navController.navigate(BottomNavItem.Productos.route)}
        )

        MainOptionButton(
            icon = Icons.Default.Person,
            label = "Mi Perfil",
            onClick = {navController.navigate(BottomNavItem.Usuario.route)}

        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun MainOptionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFB8C00))
    ) {
        Icon(icon, contentDescription = label)
        Spacer(Modifier.width(8.dp))
        Text(label)
    }
}

