package com.example.proyectofinal123.presentation

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaResultado(
    nombreCultivo: String,
    nombrePlaga: String = "Tizón Tardío", // Esto luego vendrá de tu base de datos Room
    tratamiento: String = "Reduzca el riego y retire las hojas afectadas. Si avanza, aplique un fungicida a base de cobre respetando las dosis caseras.",
    onFinalizar: () -> Unit,
    onVolverAtras: () -> Unit
) {
    val context = LocalContext.current

    // Estado para el mensaje editable. Se prellena automáticamente.
    var mensajeWhatsApp by remember {
        mutableStateOf("Hola, mi cultivo de $nombreCultivo en casa parece tener $nombrePlaga. ¿Podría asesorarme con el tratamiento?")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Diagnóstico Final",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolverAtras) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Permite hacer scroll si la pantalla es pequeña
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. TARJETA DE RESULTADO (La Enfermedad)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completado",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Problema Detectado:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = nombrePlaga,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = tratamiento,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            //  ZONA DE CONSULTA POR WHATSAPP
            Text(
                text = "Consultar con su Agrónomo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Caja de texto editable por el usuario
            OutlinedTextField(
                value = mensajeWhatsApp,
                onValueChange = { nuevoTexto -> mensajeWhatsApp = nuevoTexto },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                label = { Text("Mensaje a enviar") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Botón que lanza la API de Android hacia WhatsApp
            Button(
                onClick = {
                    try {
                        // El número de teléfono iría aquí (Ej: "591XXXXXXXX")
                        // Para el prototipo, lo dejamos vacío para que el usuario elija el contacto en WhatsApp
                        val url = "https://wa.me/?text=${Uri.encode(mensajeWhatsApp)}"
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(url)
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "WhatsApp no está instalado", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Enviar", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Enviar consulta por WhatsApp")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 3. BOTÓN FINALIZAR (Guardar en Room en el futuro)
            OutlinedButton(
                onClick = onFinalizar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar en historial y finalizar")
            }
        }
    }
}