package com.example.ultimamilla.propuesta.Location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class LocationManager(
    context: Context
) {
    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(
            context.applicationContext
        )

    @SuppressLint("MissingPermission")
    fun obtenerUbicacion(
        onSuccess: (Location) -> Unit,
        onError: (Exception) -> Unit
    ) {

        val cancellationTokenSource =
            CancellationTokenSource()

        fusedLocationClient
            .getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            )
            .addOnSuccessListener { location ->

                if (location != null) {

                    onSuccess(location)

                } else {

                    onError(
                        Exception(
                            "No se pudo obtener la ubicación"
                        )
                    )
                }
            }
            .addOnFailureListener { exception ->

                onError(exception)
            }
    }
}