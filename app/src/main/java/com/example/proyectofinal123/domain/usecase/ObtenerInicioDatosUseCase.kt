package com.example.proyectofinal123.domain.usecase

import com.example.proyectofinal123.data.repository.AgroRepository
import com.example.proyectofinal123.domain.models.categoriamodels
import com.example.proyectofinal123.domain.models.cultivomodels
import kotlinx.coroutines.delay

class ObtenerInicioDatosUseCase(private val repository: AgroRepository) {
    suspend operator fun invoke(): Pair<List<categoriamodels>, List<cultivomodels>> {
        var categorias = repository.obtenerCategorias()
        var intentos = 0

        while (categorias.isEmpty() && intentos < 5) {
            delay(1000)
            categorias = repository.obtenerCategorias()
            intentos++
        }

        val cultivos = mutableListOf<cultivomodels>()
        for (cat in categorias) {
            cultivos.addAll(repository.obtenerCultivosPorCategoria(cat.idCategoria))
        }
        return Pair(categorias, cultivos)
    }
}