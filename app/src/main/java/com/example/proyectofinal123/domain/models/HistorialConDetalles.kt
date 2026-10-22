package com.example.proyectofinal123.domain.models

data class HistorialConDetalles(
    val idHistorial: Int,
    val fechaHora: Long,
    val rutaImagen: String,
    val nombreCultivo: String,
    val nombrePlaga: String
)
