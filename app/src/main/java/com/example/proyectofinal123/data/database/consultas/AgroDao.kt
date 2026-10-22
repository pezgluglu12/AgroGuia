package com.example.proyectofinal123.data.database.consultas

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectofinal123.data.database.tablas.*

@Dao
interface AgroDao {

    // --- CONSULTAS PARA EL ÁRBOL DE DECISIÓN ---

    @Query("SELECT * FROM categoria")
    suspend fun obtenerCategorias(): List<CategoriaEntity>

    @Query("SELECT * FROM cultivo WHERE idCategoria = :idCat")
    suspend fun obtenerCultivosPorCategoria(idCat: Int): List<CultivoEntity>

    @Query("SELECT * FROM pregunta WHERE idPregunta = :idPreg")
    suspend fun obtenerPreguntaPorId(idPreg: Int): PreguntaEntity?

    @Query("SELECT * FROM respuesta WHERE idPregunta = :idPreg")
    suspend fun obtenerRespuestasDePregunta(idPreg: Int): List<RespuestaEntity>

    @Query("SELECT * FROM transicion WHERE idRespuesta = :idResp")
    suspend fun obtenerTransicionPorRespuesta(idResp: Int): TransicionEntity?

    @Query("SELECT * FROM diagnostico WHERE idDiagnostico = :idDiag")
    suspend fun obtenerDiagnosticoPorId(idDiag: Int): DiagnosticoEntity?


    // --- INSERCIONES INICIALES (Para poblar datos de prueba en la BD) ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCategorias(categorias: List<CategoriaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCultivos(cultivos: List<CultivoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPreguntas(preguntas: List<PreguntaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarRespuestas(respuestas: List<RespuestaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTransiciones(transiciones: List<TransicionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarDiagnosticos(diagnosticos: List<DiagnosticoEntity>)


    // --- GESTIÓN DEL HISTORIAL ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarHistorial(historial: HistorialEntity)

    @Query("SELECT * FROM historial")
    suspend fun obtenerHistorial(): List<HistorialEntity>

    @Query("""
        SELECT h.idHistorial, h.fechaHora, h.rutaImagen, c.nombre AS nombreCultivo, d.nombrePlaga 
        FROM historial h 
        INNER JOIN cultivo c ON h.idCultivo = c.idCultivo 
        INNER JOIN diagnostico d ON h.idDiagnostico = d.idDiagnostico
        ORDER BY h.fechaHora DESC
    """)
    suspend fun obtenerHistorialConDetalles(): List<com.example.proyectofinal123.domain.models.HistorialConDetalles>
}