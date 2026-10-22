package com.example.proyectofinal123.data.repository

import android.util.Log
import com.example.proyectofinal123.data.database.consultas.AgroDao
import com.example.proyectofinal123.data.database.tablas.HistorialEntity
import com.example.proyectofinal123.data.remote.FirebaseCultivoService
import com.example.proyectofinal123.domain.models.*

class AgroRepository(private val agroDao: AgroDao) {

    private val firebaseService = FirebaseCultivoService()

    // Memoria RAM para almacenar el árbol del cultivo seleccionado de internet
    private var arbolNubeActivo: Map<String, Any>? = null

    suspend fun prepararCultivoNube(idCultivo: Int): Boolean {
        arbolNubeActivo = firebaseService.obtenerArbolCompleto(idCultivo)
        return arbolNubeActivo != null
    }

    suspend fun obtenerCategorias(): List<categoriamodels> {
        return agroDao.obtenerCategorias().map {
            categoriamodels(it.idCategoria, it.nombre, it.descripcion)
        }
    }

    suspend fun obtenerCultivosPorCategoria(idCategoria: Int): List<cultivomodels> {
        val locales = agroDao.obtenerCultivosPorCategoria(idCategoria).map {
            cultivomodels(
                idCultivo = it.idCultivo,
                idCategoria = it.idCategoria,
                nombre = it.nombre,
                idPreguntaInicial = it.idPreguntaInicial,
                descripcion = it.descripcion
            )
        }

        Log.d("FIREBASE_TEST", "Buscando en Firebase para categoría: $idCategoria")
        val nubeRaw = firebaseService.obtenerCultivosNube()

        val nube = nubeRaw
            .filter { it["idCategoria"].toString().toIntOrNull() == idCategoria }
            .map { data ->
                cultivomodels(
                    idCultivo = data["idCultivo"].toString().toInt(),
                    idCategoria = data["idCategoria"].toString().toInt(),
                    nombre = data["nombre"].toString(),
                    idPreguntaInicial = data["idPreguntaInicial"]?.toString()?.toIntOrNull() ?: 1,
                    descripcion = data["descripcion"].toString()
                )
            }

        return locales + nube
    }

    // --- LAS 4 FUNCIONES REEMPLAZADAS (HÍBRIDAS) ---

    suspend fun obtenerPregunta(idPregunta: Int): preguntamodels? {
        arbolNubeActivo?.let { arbol ->
            val preguntasNube = arbol["preguntas"] as? List<Map<String, Any>>
            val preguntaEncontrada = preguntasNube?.find { it["idPregunta"].toString().toInt() == idPregunta }
            if (preguntaEncontrada != null) {
                return preguntamodels(
                    idPregunta = preguntaEncontrada["idPregunta"].toString().toInt(),
                    idCultivo = preguntaEncontrada["idCultivo"].toString().toInt(),
                    textoPregunta = preguntaEncontrada["textoPregunta"].toString()
                )
            }
        }
        val entidad = agroDao.obtenerPreguntaPorId(idPregunta) ?: return null
        return preguntamodels(entidad.idPregunta, entidad.idCultivo, entidad.textoPregunta)
    }

    suspend fun obtenerRespuestasDePregunta(idPregunta: Int): List<respuestamodels> {
        arbolNubeActivo?.let { arbol ->
            val respuestasNube = arbol["respuestas"] as? List<Map<String, Any>>
            val respuestasFiltradas = respuestasNube?.filter { it["idPregunta"].toString().toInt() == idPregunta }
            if (!respuestasFiltradas.isNullOrEmpty()) {
                return respuestasFiltradas.map {
                    respuestamodels(
                        idRespuesta = it["idRespuesta"].toString().toInt(),
                        idPregunta = it["idPregunta"].toString().toInt(),
                        textoRespuesta = it["textoRespuesta"].toString()
                    )
                }
            }
        }
        return agroDao.obtenerRespuestasDePregunta(idPregunta).map {
            respuestamodels(it.idRespuesta, it.idPregunta, it.textoRespuesta)
        }
    }

    suspend fun obtenerTransicion(idRespuesta: Int): trancisionmodels? {
        arbolNubeActivo?.let { arbol ->
            val transicionesNube = arbol["transiciones"] as? List<Map<String, Any>>
            val transicionEncontrada = transicionesNube?.find { it["idRespuesta"].toString().toInt() == idRespuesta }
            if (transicionEncontrada != null) {
                return trancisionmodels(
                    idTransicion = transicionEncontrada["idTransicion"].toString().toInt(),
                    idRespuesta = transicionEncontrada["idRespuesta"].toString().toInt(),
                    tipoDestino = transicionEncontrada["tipoDestino"].toString(),
                    idDestino = transicionEncontrada["idDestino"].toString().toInt()
                )
            }
        }
        val entidad = agroDao.obtenerTransicionPorRespuesta(idRespuesta) ?: return null
        return trancisionmodels(entidad.idTransicion, entidad.idRespuesta, entidad.tipoDestino, entidad.idDestino)
    }

    suspend fun obtenerDiagnostico(idDiagnostico: Int): diagnosticomodels? {
        arbolNubeActivo?.let { arbol ->
            val diagnosticosNube = arbol["diagnosticos"] as? List<Map<String, Any>>
            val diagEncontrado = diagnosticosNube?.find { it["idDiagnostico"].toString().toInt() == idDiagnostico }
            if (diagEncontrado != null) {
                return diagnosticomodels(
                    idDiagnostico = diagEncontrado["idDiagnostico"].toString().toInt(),
                    nombrePlaga = diagEncontrado["nombrePlaga"].toString(),
                    descripcion = diagEncontrado["descripcion"].toString(),
                    tratamiento = diagEncontrado["tratamiento"].toString()
                )
            }
        }
        val entidad = agroDao.obtenerDiagnosticoPorId(idDiagnostico) ?: return null
        return diagnosticomodels(entidad.idDiagnostico, entidad.nombrePlaga, entidad.descripcion, entidad.tratamiento)
    }

    // --- FUNCIONES DE HISTORIAL ---

    suspend fun guardarHistorial(fechaHora: Long, rutaImagen: String, idCultivo: Int, idDiagnostico: Int) {
        val entidad = HistorialEntity(
            fechaHora = fechaHora,
            rutaImagen = rutaImagen,
            idCultivo = idCultivo,
            idDiagnostico = idDiagnostico
        )
        agroDao.insertarHistorial(entidad)
    }

    suspend fun obtenerHistorialConDetalles(): List<HistorialConDetalles> {
        return agroDao.obtenerHistorialConDetalles()
    }
}