package com.example.proyectofinal123.presentation

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import android.graphics.Bitmap
import com.example.proyectofinal123.domain.models.categoriamodels
import com.example.proyectofinal123.domain.models.cultivomodels
import com.example.proyectofinal123.data.remote.ImagenremoteService
import com.example.proyectofinal123.presentation.viewmodel.AgroViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCultivo(
    agroViewModel: AgroViewModel = viewModel(),
    onIniciarConsulta: (cultivomodels) -> Unit,
    onVerHistorial: () -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    val context = LocalContext.current

    val categorias = agroViewModel.categorias
    val cultivosFiltradosViewModel = agroViewModel.cultivos

    var categoriaSeleccionada by remember { mutableStateOf<categoriamodels?>(null) }
    var cultivoSeleccionado by remember { mutableStateOf<cultivomodels?>(null) }

    var mostrarMenuCategorias by remember { mutableStateOf(false) }
    var mostrarMenuCultivos by remember { mutableStateOf(false) }

    var imagenCapturada by remember { mutableStateOf<Bitmap?>(null) }

    val coroutineScope = rememberCoroutineScope()
    var estaSubiendo by remember { mutableStateOf(false) }

    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            imagenCapturada = bitmap
        }
    }

    val cultivosFiltrados = remember(categoriaSeleccionada, cultivosFiltradosViewModel) {
        if (categoriaSeleccionada == null) emptyList()
        else cultivosFiltradosViewModel.filter { it.idCategoria == categoriaSeleccionada!!.idCategoria }
    }

    LaunchedEffect(categoriaSeleccionada) {
        cultivoSeleccionado = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Menú Principal", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        Toast.makeText(context, "Abriendo Menú Principal...", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                },
                actions = {
                    IconButton(onClick = onVerHistorial) {
                        Icon(Icons.Default.History, contentDescription = "Ver Historial")
                    }
                    IconButton(onClick = onCerrarSesion) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { mostrarMenuCategorias = true },
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Categoría", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                                Text(
                                    text = categoriaSeleccionada?.nombre ?: "Seleccionar",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Desplegar")
                        }
                    }

                    DropdownMenu(
                        expanded = mostrarMenuCategorias,
                        onDismissRequest = { mostrarMenuCategorias = false },
                        modifier = Modifier.fillMaxWidth(0.45f)
                    ) {
                        categorias.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria.nombre) },
                                onClick = {
                                    categoriaSeleccionada = categoria
                                    mostrarMenuCategorias = false
                                }
                            )
                        }
                    }
                }

                val estaHabilitado = categoriaSeleccionada != null
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = estaHabilitado) { mostrarMenuCultivos = true },
                        border = BorderStroke(
                            1.dp,
                            if (estaHabilitado) MaterialTheme.colorScheme.outline else Color.LightGray
                        ),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = if (estaHabilitado) Color.Transparent else Color(0xFFF1F1F1)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Cultivo", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                                Text(
                                    text = if (!estaHabilitado) "Bloqueado" else (cultivoSeleccionado?.nombre ?: "Seleccionar"),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (estaHabilitado) Color.Unspecified else Color.Gray
                                )
                            }
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Desplegar",
                                tint = if (estaHabilitado) Color.Black else Color.Gray
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = mostrarMenuCultivos && estaHabilitado,
                        onDismissRequest = { mostrarMenuCultivos = false },
                        modifier = Modifier.fillMaxWidth(0.45f)
                    ) {
                        cultivosFiltrados.forEach { cultivo ->
                            DropdownMenuItem(
                                text = { Text(cultivo.nombre) },
                                onClick = {
                                    cultivoSeleccionado = cultivo
                                    mostrarMenuCultivos = false
                                }
                            )
                        }
                    }
                }
            }

            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(vertical = 24.dp)
                    .clickable {
                        camaraLauncher.launch(null)
                    },
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (imagenCapturada != null) {
                    Image(
                        bitmap = imagenCapturada!!.asImageBitmap(),
                        contentDescription = "Fotografía capturada",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Cámara",
                            modifier = Modifier.size(72.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Presione aquí para tomar fotografía de la enfermedad",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "(Opcional)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            Button(
                onClick = {
                    if (cultivoSeleccionado != null) {
                        if (imagenCapturada != null) {
                            coroutineScope.launch {
                                estaSubiendo = true
                                val service = ImagenremoteService()
                                val resultado = service.subirImagenCloudinary(imagenCapturada!!)
                                estaSubiendo = false

                                if (resultado.isSuccess) {
                                    val urlCloudinary = resultado.getOrNull() ?: ""
                                    agroViewModel.urlImagenTemporal = urlCloudinary
                                    imagenCapturada = null
                                    Toast.makeText(context, "Imagen subida a Cloudinary", Toast.LENGTH_SHORT).show()

                                    agroViewModel.prepararDiagnostico(cultivoSeleccionado!!) {
                                        onIniciarConsulta(cultivoSeleccionado!!)
                                    }
                                } else {
                                    Toast.makeText(context, "Error al subir imagen. Revise su conexión o Cloudinary", Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            agroViewModel.urlImagenTemporal = ""
                            agroViewModel.prepararDiagnostico(cultivoSeleccionado!!) {
                                onIniciarConsulta(cultivoSeleccionado!!)
                            }
                        }
                    } else {
                        Toast.makeText(context, "Por favor seleccione categoría y cultivo primero", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !estaSubiendo && !agroViewModel.estaCargandoNube
            ) {
                if (estaSubiendo || agroViewModel.estaCargandoNube) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (estaSubiendo) "Subiendo imagen..." else "Descargando datos...",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Iniciar Consulta",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}