package com.example.xucasalonapp.ui.mainscreen.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SalonDescription(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Xuca Salón",
            fontSize = 25.sp,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Bienvenidos a Xuca Salón. Nos dedicamos a cuidar de tu imagen con los mejores productos y el mejor equipo. ¡Tu belleza es nuestra pasión!",
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 16.sp

        )
    }
}

