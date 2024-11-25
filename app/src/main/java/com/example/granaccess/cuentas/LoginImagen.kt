package com.example.granaccess.cuentas

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.granaccess.R
import com.example.granaccess.cuentas.ImageAdapter
import com.example.granaccess.cuentas.VistaAlumno
import com.google.firebase.firestore.FirebaseFirestore

class LoginImagen : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var imageUrls: List<String>
    private lateinit var correctSequence: List<String>
    private val selectedSequence = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_imagen)

        db = FirebaseFirestore.getInstance()
        userId = intent.getStringExtra("userId") ?: ""

        val gridView = findViewById<GridView>(R.id.gridImagenes)

        // Cargar imágenes del usuario
        db.collection("usuarios").document(userId).get()
            .addOnSuccessListener { document ->
                imageUrls = document.get("imagenes") as List<String>
                correctSequence = document.get("secuencia") as List<String>

                // Configurar el GridView con las imágenes
                gridView.adapter = ImageAdapter(this, imageUrls.map { Uri.parse(it) })
                gridView.setOnItemClickListener { _, _, position, _ ->
                    val selectedImage = imageUrls[position]
                    selectedSequence.add(selectedImage)

                    if (selectedSequence.size == 2) {
                        validarSecuencia()
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar imágenes: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun validarSecuencia() {
        if (selectedSequence == correctSequence) {
            Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, VistaAlumno::class.java))
            finish()
        } else {
            Toast.makeText(this, "Secuencia incorrecta. Intenta nuevamente.", Toast.LENGTH_SHORT).show()
            selectedSequence.clear()
        }
    }
}
