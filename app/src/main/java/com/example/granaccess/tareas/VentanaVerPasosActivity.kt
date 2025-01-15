package com.example.granaccess.tareas

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.granaccess.R
import com.google.firebase.firestore.FirebaseFirestore

class VentanaVerPasosActivity : AppCompatActivity() {

    private lateinit var textNumeroPaso: TextView
    private lateinit var textDescripcionPaso: TextView
    private lateinit var btnPasoSiguiente: Button
    private lateinit var btnPasoAnterior: Button
    private val db = FirebaseFirestore.getInstance()

    private var pasosList = mutableListOf<Map<String, Any>>() // Cambiado a MutableList
    private var currentStepIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_pasos)

        textNumeroPaso = findViewById(R.id.textNumeroPaso)
        textDescripcionPaso = findViewById(R.id.textDescripcionPaso)
        btnPasoSiguiente = findViewById(R.id.btnPasoSiguiente)
        btnPasoAnterior = findViewById(R.id.btnPasoAnterior)

        val tareaId = intent.getStringExtra("tareaId")

        if (tareaId != null) {
            cargarPasosDesdeFirestore(tareaId)
        } else {
            Toast.makeText(this, "No se encontró la tarea seleccionada", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnPasoSiguiente.setOnClickListener {
            if (currentStepIndex < pasosList.size - 1) {
                currentStepIndex++
                mostrarPasoActual()
            }
        }

        btnPasoAnterior.setOnClickListener {
            if (currentStepIndex > 0) {
                currentStepIndex--
                mostrarPasoActual()
            }
        }
    }

    private fun cargarPasosDesdeFirestore(tareaId: String) {
        db.collection("tareas")
            .document(tareaId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val pasosFirestore = document.get("pasos") as? List<Map<String, Any>> // Cambiado a List
                    if (!pasosFirestore.isNullOrEmpty()) {
                        pasosList = pasosFirestore.toMutableList()
                        mostrarPasoActual()
                    } else {
                        Toast.makeText(this, "No hay pasos registrados para esta tarea", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    Toast.makeText(this, "Tarea no encontrada", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar pasos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mostrarPasoActual() {
        val paso = pasosList[currentStepIndex]
        val numeroPaso = paso["numeropaso"]?.toString() ?: "?"
        val descripcionPaso = paso["descripcionpaso"]?.toString() ?: "Sin descripción"

        textNumeroPaso.text = "Paso $numeroPaso"
        textDescripcionPaso.text = descripcionPaso

        btnPasoAnterior.isEnabled = currentStepIndex > 0
        btnPasoSiguiente.isEnabled = currentStepIndex < pasosList.size - 1
    }
}
