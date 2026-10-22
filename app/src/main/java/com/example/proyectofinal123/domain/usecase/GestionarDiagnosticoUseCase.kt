package com.example.proyectofinal123.domain.usecase

import com.example.proyectofinal123.data.repository.AgroRepository
import com.example.proyectofinal123.domain.models.diagnosticomodels

class GestionarDiagnosticoUseCase(private val repository: AgroRepository) {
    suspend operator fun invoke(idDiagnostico: Int): diagnosticomodels? {
        return repository.obtenerDiagnostico(idDiagnostico)
    }
}