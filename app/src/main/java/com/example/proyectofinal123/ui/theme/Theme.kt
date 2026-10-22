package com.example.proyectofinal123.ui.theme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Asignamos nuestros colores a las variables de Material 3
private val AgroGuiaColorScheme = lightColorScheme(
    primary = VerdeBoton,                // Color principal (Botones)
    onPrimary = androidx.compose.ui.graphics.Color.White, // Texto sobre botones
    primaryContainer = VerdeTopBar,      // Color de la barra superior (TopAppBar)
    onPrimaryContainer = androidx.compose.ui.graphics.Color.Black, // Texto en la barra superior

    secondary = MarronIconos,            // Color secundario (Iconos de cámara)
    secondaryContainer = CaquiSeleccionado, // Fondos de elementos activos/seleccionados

    background = FondoBeige,             // Fondo general de toda la pantalla
    surface = FondoBeige,                // Fondo de contenedores base
    surfaceVariant = CaquiClaro,         // Fondo de las tarjetas desplegables

    outline = CaquiSeleccionado          // Color de los bordes de las tarjetas
)

@Composable
fun ProyectoFinal123Theme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AgroGuiaColorScheme,
        typography = Typography, // Usa la tipografía por defecto que ya tienes
        content = content
    )
}