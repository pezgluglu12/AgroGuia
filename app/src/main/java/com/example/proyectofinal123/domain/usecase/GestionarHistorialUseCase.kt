package com.example.proyectofinal123.domain.usecase

import com.example.proyectofinal123.data.repository.AgroRepository
import com.example.proyectofinal123.domain.models.HistorialConDetalles

class GestionarHistorialUseCase(private val repository: AgroRepository) {
    suspend fun guardar(idCultivo: Int, idDiagnostico: Int, rutaImagen: String) {
        repository.guardarHistorial(
            fechaHora = System.currentTimeMillis(),
            rutaImagen = rutaImagen,
            idCultivo = idCultivo,
            idDiagnostico = idDiagnostico
        )
    }

    suspend fun obtener(): List<HistorialConDetalles> {
        return repository.obtenerHistorialConDetalles()
    }
}