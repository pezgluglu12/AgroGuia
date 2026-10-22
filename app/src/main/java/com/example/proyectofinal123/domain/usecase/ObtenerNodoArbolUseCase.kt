package com.example.proyectofinal123.domain.usecase

import com.example.proyectofinal123.data.repository.AgroRepository
import com.example.proyectofinal123.domain.models.preguntamodels
import com.example.proyectofinal123.domain.models.respuestamodels

class ObtenerNodoArbolUseCase(private val repository: AgroRepository) {
    suspend operator fun invoke(idPregunta: Int): Pair<preguntamodels?, List<respuestamodels>> {
        val pregunta = repository.obtenerPregunta(idPregunta)
        val respuestas = repository.obtenerRespuestasDePregunta(idPregunta)
        return Pair(pregunta, respuestas)
    }
}