
package com.example.proyectofinal123.data.database.tablas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cultivo")
data class CultivoEntity(
    @PrimaryKey(autoGenerate = true)
    val idCultivo: Int = 0,
    val idCategoria: Int,
    val nombre: String,
    val descripcion: String,
    val idPreguntaInicial: Int
)