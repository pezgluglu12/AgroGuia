package com.example.proyectofinal123.data.database.tablas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diagnostico")
data class DiagnosticoEntity(
    @PrimaryKey(autoGenerate = true)
    val idDiagnostico: Int = 0,
    val nombrePlaga: String,
    val descripcion: String,
    val tratamiento: String
)