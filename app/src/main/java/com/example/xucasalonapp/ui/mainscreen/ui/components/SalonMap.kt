package com.example.xucasalonapp.ui.mainscreen.ui.components


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.LatLng
@Composable
fun SalonMap(modifier: Modifier = Modifier) {
    val salonLocation = LatLng(43.31592796725584, -8.370890973009276)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(salonLocation, 15f)
    }

    Box(modifier = modifier) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = MarkerState(position = salonLocation),
                title = "Xuca Salón",
                snippet = "Tu salón de belleza de confianza"
            )
        }
    }
}

