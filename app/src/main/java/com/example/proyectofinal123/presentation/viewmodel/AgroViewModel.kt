package com.example.proyectofinal123.presentation.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal123.data.database.AgroDatabase
import com.example.proyectofinal123.data.repository.AgroRepository
import com.example.proyectofinal123.domain.models.*
import com.example.proyectofinal123.domain.usecase.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AgroViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AgroDatabase.obtenerBaseDeDatos(application)
    private val repository = AgroRepository(database.agroDao())

    // Inyección manual de Casos de Uso
    private val obtenerInicioDatosUseCase = ObtenerInicioDatosUseCase(repository)
    private val obtenerNodoArbolUseCase = ObtenerNodoArbolUseCase(repository)
    private val evaluarTransicionUseCase = EvaluarTransicionUseCase(repository)
    private val gestionarDiagnosticoUseCase = GestionarDiagnosticoUseCase(repository)
    private val gestionarHistorialUseCase = GestionarHistorialUseCase(repository)

    var categorias by mutableStateOf<List<categoriamodels>>(emptyList())
        private set
    var cultivos by mutableStateOf<List<cultivomodels>>(emptyList())
        private set
    var preguntaActual by mutableStateOf<preguntamodels?>(null)
        private set
    var opcionesRespuesta by mutableStateOf<List<respuestamodels>>(emptyList())
        private set
    var diagnosticoFinal by mutableStateOf<diagnosticomodels?>(null)
        private set
    var historial by mutableStateOf<List<HistorialConDetalles>>(emptyList())
        private set
    var urlImagenTemporal by mutableStateOf("")

    // NUEVO: Estado de carga para la nube
    var estaCargandoNube by mutableStateOf(false)
        private set

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        viewModelScope.launch(Dispatchers.IO) {
            val (listaCat, listaCultivos) = obtenerInicioDatosUseCase()
            withContext(Dispatchers.Main) {
                categorias = listaCat
                cultivos = listaCultivos
            }
        }
    }

    // NUEVO: Función para descargar el árbol antes de navegar a la pregunta
    fun prepararDiagnostico(cultivo: cultivomodels, onListoParaNavegar: (Int) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            // Activamos la pantalla de carga
            withContext(Dispatchers.Main) { estaCargandoNube = true }

            // Lógica híbrida: Asumimos que los IDs 10 o mayores son de Firebase
            if (cultivo.idCultivo >= 10) {
                repository.prepararCultivoNube(cultivo.idCultivo)
            } else {
                // Si es local (1 al 9), limpiamos la RAM enviando un ID inexistente
                repository.prepararCultivoNube(-1)
            }

            // Quitamos la carga y ordenamos navegar a la pantalla de preguntas
            withContext(Dispatchers.Main) {
                estaCargandoNube = false
                onListoParaNavegar(cultivo.idPreguntaInicial)
            }
        }
    }

    fun cargarPreguntaYOpciones(idPregunta: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val (pregunta, respuestas) = obtenerNodoArbolUseCase(idPregunta)
            withContext(Dispatchers.Main) {
                preguntaActual = pregunta
                opcionesRespuesta = respuestas
            }
        }
    }

    fun evaluarRespuestaSeleccionada(
        respuesta: respuestamodels,
        onSiguientePregunta: (Int) -> Unit,
        onLlegarADiagnostico: (Int) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val transicion = evaluarTransicionUseCase(respuesta.idRespuesta)
            withContext(Dispatchers.Main) {
                if (transicion != null) {
                    when (transicion.tipoDestino) {
                        "PREGUNTA" -> onSiguientePregunta(transicion.idDestino)
                        "DIAGNOSTICO" -> onLlegarADiagnostico(transicion.idDestino)
                    }
                }
            }
        }
    }

    fun obtenerDiagnosticoPorId(idDiagnostico: Int, onResultado: (diagnosticomodels?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val diag = gestionarDiagnosticoUseCase(idDiagnostico)
            withContext(Dispatchers.Main) {
                onResultado(diag)
            }
        }
    }

    fun registrarEnHistorial(idCultivo: Int, idDiagnostico: Int, rutaImagen: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            gestionarHistorialUseCase.guardar(idCultivo, idDiagnostico, rutaImagen)
            cargarHistorial()
        }
    }

    fun cargarHistorial() {
        viewModelScope.launch(Dispatchers.IO) {
            val listaHistorial = gestionarHistorialUseCase.obtener()
            withContext(Dispatchers.Main) {
                historial = listaHistorial
            }
        }
    }
}