package com.example.proyectofinal123.data.database.tablas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "historial")
data class HistorialEntity(
    @PrimaryKey(autoGenerate = true)
    val idHistorial: Int = 0,
    val fechaHora: Long,
    val rutaImagen: String,
    val idCultivo: Int,
    val idDiagnostico: Int
)