package com.example.proyectofinal123.domain.models

object DatosPrueba {
    val categorias = listOf(
        categoriamodels(1, "Hortalizas", "Cultivos de huerto tradicionales"),
        categoriamodels(2, "Hierbas Aromáticas", "Plantas para condimento o medicina")
    )

    // El Tomate (idCultivo = 1) inicia en la pregunta 1 (idPreguntaInicial = 1)
    val cultivos = listOf(
        cultivomodels(1, 1, "Tomate", 1, "Requiere mucho sol y riego constante"),
        cultivomodels(2, 1, "Papa", 10, "Cultivo de tubérculo"),
        cultivomodels(3, 1, "Yuca", 20, "Raíz comestible"),
        cultivomodels(4, 2, "Albahaca", 30, "Aromática, repele ciertos insectos")
    )

    // 1. TABLA DE PREGUNTAS
    val preguntas = listOf(
        preguntamodels(1, 1, "¿Qué anomalía visual presenta la hoja de la planta?"),
        preguntamodels(2, 1, "¿Cómo es el tallo y el nivel de humedad de la tierra?"),
        preguntamodels(3, 1, "¿Observa pequeños insectos debajo de las hojas?")
    )

    // 2. TABLA DE RESPUESTAS
    val respuestas = listOf(
        // Opciones para la Pregunta 1
        respuestamodels(1, 1, "Manchas oscuras con borde amarillo"),
        respuestamodels(2, 1, "Hojas enrolladas hacia arriba"),
        respuestamodels(3, 1, "Polvo blanco en el haz de la hoja"),

        // Opciones para la Pregunta 2 (Viene de elegir la respuesta 1)
        respuestamodels(4, 2, "Mucha humedad y manchas negras en el tallo"),
        respuestamodels(5, 2, "Clima seco y las manchas tienen anillos concéntricos"),

        // Opciones para la Pregunta 3 (Viene de elegir la respuesta 2)
        respuestamodels(6, 3, "Sí, hay insectos verdes o blancos"),
        respuestamodels(7, 3, "No hay insectos, pero la tierra está muy seca")
    )

    // 3. TABLA DE DIAGNÓSTICOS
    val diagnosticos = listOf(
        diagnosticomodels(
            idDiagnostico = 1,
            nombrePlaga = "Tizón Tardío",
            descripcion = "Hongo agresivo favorecido por la alta humedad.",
            tratamiento = "Reduzca el riego y retire las hojas afectadas. Si avanza, aplique un fungicida a base de cobre respetando las dosis caseras."
        ),
        diagnosticomodels(
            idDiagnostico = 2,
            nombrePlaga = "Tizón Temprano (Alternaria)",
            descripcion = "Hongo que forma anillos en las hojas más viejas.",
            tratamiento = "Mejore la ventilación de la planta, pode las hojas bajas y aplique fungicida orgánico."
        ),
        diagnosticomodels(
            idDiagnostico = 3,
            nombrePlaga = "Oídio (Ceniza)",
            descripcion = "Hongo superficial que cubre las hojas de polvo blanco.",
            tratamiento = "Aplique una solución casera de bicarbonato de sodio con agua y jabón potásico."
        ),
        diagnosticomodels(
            idDiagnostico = 4,
            nombrePlaga = "Ataque de Pulgón / Mosca Blanca",
            descripcion = "Insectos chupadores de savia que enrollan las hojas.",
            tratamiento = "Rocíe jabón potásico o aceite de Neem en el envés de las hojas cada 5 días."
        ),
        diagnosticomodels(
            idDiagnostico = 5,
            nombrePlaga = "Estrés Hídrico",
            descripcion = "La planta enrolla sus hojas para evitar perder agua.",
            tratamiento = "Aumente la frecuencia de riego de forma gradual, preferiblemente temprano en la mañana."
        )
    )

    // 4. TABLA DE TRANSICIONES (El corazón del Árbol de Decisión)
    val transiciones = listOf(
        // Desde Pregunta 1:
        trancisionmodels(1, idRespuesta = 1, tipoDestino = "PREGUNTA", idDestino = 2),    // Manchas -> Va a Pregunta 2
        trancisionmodels(2, idRespuesta = 2, tipoDestino = "PREGUNTA", idDestino = 3),    // Hojas enrolladas -> Va a Pregunta 3
        trancisionmodels(3, idRespuesta = 3, tipoDestino = "DIAGNOSTICO", idDestino = 3), // Polvo blanco -> Directo a Oídio

        // Desde Pregunta 2:
        trancisionmodels(4, idRespuesta = 4, tipoDestino = "DIAGNOSTICO", idDestino = 1), // Tizón Tardío
        trancisionmodels(5, idRespuesta = 5, tipoDestino = "DIAGNOSTICO", idDestino = 2), // Tizón Temprano

        // Desde Pregunta 3:
        trancisionmodels(6, idRespuesta = 6, tipoDestino = "DIAGNOSTICO", idDestino = 4), // Pulgón
        trancisionmodels(7, idRespuesta = 7, tipoDestino = "DIAGNOSTICO", idDestino = 5)  // Estrés Hídrico
    )

    // --- FUNCIONES DEL ALGORITMO DE VERIFICACIÓN ---

    fun obtenerPregunta(idPregunta: Int): preguntamodels? {
        return preguntas.find { it.idPregunta == idPregunta }
    }

    fun obtenerRespuestasDePregunta(idPregunta: Int): List<respuestamodels> {
        return respuestas.filter { it.idPregunta == idPregunta }
    }

    fun obtenerTransicion(idRespuesta: Int): trancisionmodels? {
        return transiciones.find { it.idRespuesta == idRespuesta }
    }

    fun obtenerDiagnostico(idDiagnostico: Int): diagnosticomodels? {
        return diagnosticos.find { it.idDiagnostico == idDiagnostico }
    }
}