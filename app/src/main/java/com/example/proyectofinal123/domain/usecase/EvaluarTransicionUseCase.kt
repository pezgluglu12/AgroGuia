package com.example.proyectofinal123.domain.usecase

import com.example.proyectofinal123.data.repository.AgroRepository
import com.example.proyectofinal123.domain.models.trancisionmodels

class EvaluarTransicionUseCase(private val repository: AgroRepository) {
    suspend operator fun invoke(idRespuesta: Int): trancisionmodels? {
        return repository.obtenerTransicion(idRespuesta)
    }
}