package com.example.proyectofinal123.data.database.tablas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "respuesta")
data class RespuestaEntity(
    @PrimaryKey(autoGenerate = true)
    val idRespuesta: Int = 0,
    val idPregunta: Int,
    val textoRespuesta: String
)