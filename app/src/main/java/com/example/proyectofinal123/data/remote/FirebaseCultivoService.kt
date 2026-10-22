package com.example.proyectofinal123.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseCultivoService {

    private val db = FirebaseFirestore.getInstance()
    // Memoria RAM para almacenar el árbol del cultivo seleccionado
    private var arbolNubeActivo: Map<String, Any>? = null

    // Descarga y guarda el árbol completo antes de iniciar el diagnóstico
    suspend fun prepararCultivoNube(idCultivo: Int): Boolean {
        arbolNubeActivo = obtenerArbolCompleto(idCultivo)
        return arbolNubeActivo != null
    }

    // 1. Descarga solo los datos básicos para mostrar en la lista de inicio
    suspend fun obtenerCultivosNube(): List<Map<String, Any>> {
        return try {
            val snapshot = db.collection("cultivo_extra").get().await()
            snapshot.documents.mapNotNull { it.data }
        } catch (e: Exception) {
            emptyList() // Si no hay internet o falla, retorna lista vacía para no romper la app
        }
    }

    // 2. Descarga el árbol completo (preguntas, respuestas) solo cuando el usuario elige ese cultivo
    suspend fun obtenerArbolCompleto(idCultivo: Int): Map<String, Any>? {
        return try {
            val snapshot = db.collection("cultivo_extra")
                .whereEqualTo("idCultivo", idCultivo)
                .get()
                .await()
            snapshot.documents.firstOrNull()?.data
        } catch (e: Exception) {
            null
        }
    }
}
