package com.example.proyectofinal123

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyectofinal123.data.remote.AuthService
import com.example.proyectofinal123.domain.models.diagnosticomodels
import com.example.proyectofinal123.domain.models.preguntamodels
import com.example.proyectofinal123.domain.models.respuestamodels
import com.example.proyectofinal123.presentation.PantallaCultivo
import com.example.proyectofinal123.presentation.PantallaLogin
import com.example.proyectofinal123.presentation.PantallaPregunta
import com.example.proyectofinal123.presentation.PantallaResultado
import com.example.proyectofinal123.presentation.PantallaHistorial
import com.example.proyectofinal123.presentation.viewmodel.AgroViewModel
import com.example.proyectofinal123.ui.theme.ProyectoFinal123Theme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectoFinal123Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val agroViewModel: AgroViewModel = viewModel()
                    val context = LocalContext.current

                    // Permisos de notificaciones (Android 13+)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val notiPermissionLauncher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.RequestPermission()
                        ) {}
                        LaunchedEffect(Unit) {
                            notiPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }

                    val usuarioActual = FirebaseAuth.getInstance().currentUser
                    val destinoInicial = if (usuarioActual != null) "seleccion_cultivo" else "login"

                    NavHost(navController = navController, startDestination = destinoInicial) {

                        // --- LOGIN ---
                        composable("login") {
                            PantallaLogin(
                                onLoginExitoso = {
                                    navController.navigate("seleccion_cultivo") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // --- SELECCIÓN DE CULTIVO ---
                        composable("seleccion_cultivo") {
                            PantallaCultivo(
                                agroViewModel = agroViewModel,
                                onIniciarConsulta = { cultivoElegido ->
                                    // Navegamos al cuestionario pasando el ID del cultivo y su primera pregunta raíz
                                    navController.navigate("cuestionario/${cultivoElegido.idCultivo}/${cultivoElegido.nombre}/${cultivoElegido.idPreguntaInicial}")
                                },
                                onVerHistorial = {
                                    navController.navigate("historial")
                                },
                                onCerrarSesion = {
                                    AuthService().cerrarSesion()
                                    navController.navigate("login") {
                                        popUpTo("seleccion_cultivo") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // --- HISTORIAL ---
                        composable("historial") {
                            PantallaHistorial(
                                agroViewModel = agroViewModel,
                                onVolverAtras = { navController.popBackStack() }
                            )
                        }

                        // --- CUESTIONARIO (ÁRBOL DE DECISIÓN) ---
                        composable(
                            route = "cuestionario/{idCultivo}/{nombrePlanta}/{idPregunta}",
                            arguments = listOf(
                                navArgument("idCultivo") { type = NavType.IntType },
                                navArgument("nombrePlanta") { type = NavType.StringType },
                                navArgument("idPregunta") { type = NavType.IntType }
                            )
                        ) { backStackEntry ->
                            val idCultivo = backStackEntry.arguments?.getInt("idCultivo") ?: 1
                            val nombreDeLaPlanta = backStackEntry.arguments?.getString("nombrePlanta") ?: ""
                            val idPregunta = backStackEntry.arguments?.getInt("idPregunta") ?: 1

                            // Cargamos pregunta y respuestas desde el ViewModel (Room)
                            LaunchedEffect(idPregunta) {
                                agroViewModel.cargarPreguntaYOpciones(idPregunta)
                            }

                            val preguntaActual = agroViewModel.preguntaActual
                            val opciones = agroViewModel.opcionesRespuesta

                            if (preguntaActual != null) {
                                PantallaPregunta(
                                    nombreCultivo = nombreDeLaPlanta,
                                    pregunta = preguntaActual,
                                    opcionesRespuesta = opciones,
                                    onSiguiente = { respuestaSeleccionada ->
                                        // Evaluamos la transición en Room
                                        agroViewModel.evaluarRespuestaSeleccionada(
                                            respuesta = respuestaSeleccionada,
                                            onSiguientePregunta = { siguienteIdPregunta ->
                                                navController.navigate("cuestionario/$idCultivo/$nombreDeLaPlanta/$siguienteIdPregunta")
                                            },
                                            onLlegarADiagnostico = { idDiagnosticoFinal ->
                                                navController.navigate("resultado/$idCultivo/$nombreDeLaPlanta/$idDiagnosticoFinal")
                                            }
                                        )
                                    },
                                    onVolverAtras = { navController.popBackStack() }
                                )
                            }
                        }

                        // --- RESULTADO ---
                        composable(
                            route = "resultado/{idCultivo}/{nombrePlanta}/{idDiagnostico}",
                            arguments = listOf(
                                navArgument("idCultivo") { type = NavType.IntType },
                                navArgument("nombrePlanta") { type = NavType.StringType },
                                navArgument("idDiagnostico") { type = NavType.IntType }
                            )
                        ) { backStackEntry ->
                            val idCultivo = backStackEntry.arguments?.getInt("idCultivo") ?: 1
                            val nombreDeLaPlanta = backStackEntry.arguments?.getString("nombrePlanta") ?: ""
                            val idDiagnostico = backStackEntry.arguments?.getInt("idDiagnostico") ?: 1

                            /* CORRECCIÓN: Se mantiene una instancia de estado local limpia para evitar nulos */
                            var diagnostico by remember { mutableStateOf<diagnosticomodels?>(null) }

                            LaunchedEffect(idDiagnostico) {
                                agroViewModel.obtenerDiagnosticoPorId(idDiagnostico) { diag ->
                                    diagnostico = diag
                                }
                            }

                            if (diagnostico != null) {
                                PantallaResultado(
                                    nombreCultivo = nombreDeLaPlanta,
                                    nombrePlaga = diagnostico!!.nombrePlaga,
                                    tratamiento = "${diagnostico!!.descripcion}\n\nRecomendación:\n${diagnostico!!.tratamiento}",
                                    onFinalizar = {
                                        // GUARDAR EN HISTORIAL (Room)
                                        agroViewModel.registrarEnHistorial(
                                            idCultivo = idCultivo,
                                            idDiagnostico = idDiagnostico,
                                            rutaImagen = agroViewModel.urlImagenTemporal
                                        )
                                        
                                        // Limpiar la URL temporal después de guardar
                                        agroViewModel.urlImagenTemporal = ""

                                        Toast.makeText(context, "Diagnóstico guardado en el historial", Toast.LENGTH_SHORT).show()

                                        navController.navigate("seleccion_cultivo") {
                                            popUpTo("seleccion_cultivo") { inclusive = true }
                                        }
                                    },
                                    onVolverAtras = { navController.popBackStack() }
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}
