package com.example.proyectofinal123.data.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthService {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun obtenerUsuarioActual(): FirebaseUser? {
        return auth.currentUser
    }

    suspend fun autenticarConGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val user = authResult.user ?: throw Exception("Error obteniendo usuario de Firebase")

            // Guardar o actualizar datos en Firestore
            guardarUsuarioEnFirestore(user)

            Result.success(user)
        } catch (e: Exception) {
            Log.e("AUTH_SERVICE", "Error autenticando con Google en Firebase", e)
            Result.failure(e)
        }
    }

    private suspend fun guardarUsuarioEnFirestore(user: FirebaseUser) {
        try {
            val uid = user.uid
            val docRef = firestore.collection("persona").document(uid)
            val snapshot = docRef.get().await()

            val datos = hashMapOf<String, Any>(
                "uid" to uid,
                "nombre" to (user.displayName ?: ""),
                "email" to (user.email ?: ""),
                "fotoUrl" to (user.photoUrl?.toString() ?: ""),
                "fechaRegistro" to (snapshot.getLong("fechaRegistro") ?: System.currentTimeMillis())
            )

            docRef.set(datos).await()
            Log.d("AUTH_SERVICE", "Usuario guardado en Firestore: persona/$uid")
        } catch (e: Exception) {
            Log.e("AUTH_SERVICE", "Error guardando usuario en Firestore", e)
        }
    }

    fun cerrarSesion() {
        auth.signOut()
    }
}
