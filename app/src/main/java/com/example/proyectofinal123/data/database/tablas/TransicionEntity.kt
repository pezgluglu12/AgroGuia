package com.example.proyectofinal123.data.database.tablas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transicion")
data class TransicionEntity(
    @PrimaryKey(autoGenerate = true)
    val idTransicion: Int = 0,
    val idRespuesta: Int,
    val tipoDestino: String, // "PREGUNTA" o "DIAGNOSTICO"
    val idDestino: Int
)