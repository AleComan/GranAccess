package com.example.granaccess.cuentas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.granaccess.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginTexto : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_texto)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnBack = findViewById<Button>(R.id.backButton)

        btnLogin.setOnClickListener {
            val email = findViewById<EditText>(R.id.etUsername).text.toString().trim()
            val password = findViewById<EditText>(R.id.etPassword).text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                iniciarSesion(email, password)
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        btnBack.setOnClickListener {
            finish() // Volver a la vista anterior
        }
    }

    private fun iniciarSesion(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    verificarRolUsuario(userId)
                } else {
                    Toast.makeText(this, "No se pudo obtener el ID del usuario.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al iniciar sesión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun verificarRolUsuario(userId: String) {
        db.collection("usuarios").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val role = document.getString("role")
                    val username = document.getString("username") ?: "Usuario"
                    when (role) {
                        "admin" -> {
                            val intent = Intent(this, VistaAdmin::class.java)
                            intent.putExtra("userID", userId)
                            intent.putExtra("username", username)
                            startActivity(intent)
                            finish()
                        }
                        "profesor" -> {
                            val intent = Intent(this, VistaProfesor::class.java)
                            intent.putExtra("userID", userId)
                            intent.putExtra("username", username)
                            startActivity(intent)
                            finish()
                        }
                        "alumno" -> {
                            val intent = Intent(this, VistaAlumnoTexto::class.java)
                            intent.putExtra("userID", userId)
                            intent.putExtra("username", username)
                            startActivity(intent)
                            finish()
                        }
                        else -> {
                            Toast.makeText(this, "Rol desconocido para este usuario.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "No se encontró información para este usuario.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener datos del usuario: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
