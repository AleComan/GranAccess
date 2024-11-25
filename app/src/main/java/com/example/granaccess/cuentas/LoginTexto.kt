package com.example.granaccess.cuentas

import android.annotation.SuppressLint
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

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_texto)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnBack = findViewById<Button>(R.id.backButton)

        btnLogin.setOnClickListener {
            val usuario = findViewById<EditText>(R.id.etUsuario).text.toString()
            val password = findViewById<EditText>(R.id.etPassword).text.toString()

            if (usuario.isNotEmpty() && password.isNotEmpty()) {
                iniciarSesion(usuario, password)
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        btnBack.setOnClickListener {
            startActivity(Intent(this, SeleccionarUsuario::class.java))
            finish()
        }
    }

    private fun iniciarSesion(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val userId = auth.currentUser?.uid ?: return@addOnSuccessListener
                db.collection("usuarios").document(userId).get()
                    .addOnSuccessListener { document ->
                        val role = document.getString("role")

                        when (role) {
                            "admin" -> {
                                startActivity(Intent(this, VistaAdmin::class.java))
                                finish()
                            }
                            "profesor" -> {
                                startActivity(Intent(this, VistaProfesor::class.java))
                                finish()
                            }
                            "alumno" -> {
                                startActivity(Intent(this, VistaAlumno::class.java))
                                finish()
                            }
                            else -> {
                                Toast.makeText(this, "Rol desconocido", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al obtener datos del usuario: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al iniciar sesión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
