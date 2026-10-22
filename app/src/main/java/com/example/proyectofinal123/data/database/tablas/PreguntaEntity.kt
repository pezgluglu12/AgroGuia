package com.example.proyectofinal123.data.database.tablas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pregunta")
data class PreguntaEntity(
    @PrimaryKey(autoGenerate = true)
    val idPregunta: Int = 0,
    val idCultivo: Int,
    val textoPregunta: String
)
