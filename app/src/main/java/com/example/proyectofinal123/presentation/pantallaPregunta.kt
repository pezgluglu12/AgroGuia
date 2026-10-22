package com.example.proyectofinal123.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.proyectofinal123.domain.models.preguntamodels
import com.example.proyectofinal123.domain.models.respuestamodels

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPregunta(
    nombreCultivo: String,
    pregunta: preguntamodels,
    opcionesRespuesta: List<respuestamodels>,
    onSiguiente: (respuestamodels) -> Unit, // Ahora devuelve el objeto respuesta completo
    onVolverAtras: () -> Unit
) {
    // Se reinicia a null cada vez que cambia la pregunta
    var opcionSeleccionada by remember(pregunta.idPregunta) { mutableStateOf<respuestamodels?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Evaluando: $nombreCultivo",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolverAtras) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = pregunta.textoPregunta, // <- Texto dinámico del modelo
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(24.dp)
                )
            }

            Text(
                text = "Seleccione la opción que mejor describa el problema:",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                textAlign = TextAlign.Start
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(opcionesRespuesta) { respuesta ->
                    val estaSeleccionado = (opcionSeleccionada?.idRespuesta == respuesta.idRespuesta)

                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { opcionSeleccionada = respuesta },
                        border = BorderStroke(
                            1.dp,
                            if (estaSeleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        ),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = if (estaSeleccionado) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = estaSeleccionado,
                                onClick = { opcionSeleccionada = respuesta }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = respuesta.textoRespuesta, // <- Texto de la respuesta del modelo
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (estaSeleccionado) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    opcionSeleccionada?.let { onSiguiente(it) }
                },
                enabled = opcionSeleccionada != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Siguiente",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}