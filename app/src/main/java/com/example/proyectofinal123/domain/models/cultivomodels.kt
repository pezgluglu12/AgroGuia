package com.example.proyectofinal123.domain.models

data class cultivomodels(
    val idCultivo: Int = 0,
    val idCategoria: Int,
    val nombre: String,
    val idPreguntaInicial: Int= 1,
    val descripcion: String
)