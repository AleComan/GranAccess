package com.example.granaccess.tareas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.granaccess.R
import com.google.firebase.firestore.FirebaseFirestore

class VerTareaActivity : AppCompatActivity() {

    private lateinit var textTituloTarea: TextView
    private lateinit var textDescripcionTarea: TextView
    private lateinit var btnVerPasos: Button
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_tarea)

        // Inicializar vistas
        textTituloTarea = findViewById(R.id.textTituloTarea)
        textDescripcionTarea = findViewById(R.id.textDescripcionTarea)
        btnVerPasos = findViewById(R.id.btnVerPasos)

        // Obtener el ID de la tarea seleccionada
        val tareaId = intent.getStringExtra("tareaSeleccionada")

        if (tareaId != null) {
            cargarTareaDesdeFirestore(tareaId)

            // Configurar el botón "Ver Pasos"
            btnVerPasos.setOnClickListener {
                // Crear el intent para redirigir a VentanaVerPasosActivity
                val intent = Intent(this, VentanaVerPasosActivity::class.java)
                intent.putExtra("tareaId", tareaId) // Pasar el ID de la tarea seleccionada
                startActivity(intent) // Lanzar la actividad
            }
        } else {
            Toast.makeText(this, "No se encontró la tarea seleccionada", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun cargarTareaDesdeFirestore(tareaId: String) {
        db.collection("tareas")
            .document(tareaId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Mostrar los datos de la tarea
                    textTituloTarea.text = document.getString("titulo") ?: "Sin título"
                    textDescripcionTarea.text = document.getString("descripcion") ?: "Sin descripción"
                } else {
                    Toast.makeText(this, "No se encontró la tarea en la base de datos", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar tarea: ${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
    }
}
