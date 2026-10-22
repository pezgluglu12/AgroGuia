package com.example.proyectofinal123.domain.models

data class historialmodels (
    val idHistorial: Int = 0,
    val fechaHora: Long,
    val rutaImagen: String,    // Ruta local de la foto en el dispositivo
    val idCultivo: Int,
    val idDiagnostico: Int
)