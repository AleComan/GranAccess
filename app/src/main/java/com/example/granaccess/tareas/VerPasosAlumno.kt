package com.example.granaccess.tareas

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.granaccess.R
import com.google.firebase.firestore.FirebaseFirestore

class VerPasosAlumno : AppCompatActivity() {

    private lateinit var numeroPasoTextView: TextView
    private lateinit var descripcionPasoTextView: TextView
    private lateinit var imagenPasoImageView: ImageView
    private lateinit var btnAnteriorPaso: Button
    private lateinit var btnSiguientePaso: Button
    private lateinit var btnMarcarCompletada: Button
    private lateinit var btnVolver: Button

    private var pasos: ArrayList<PasoTarea> = arrayListOf()
    private var currentPasoIndex: Int = 0
    private var tareaID: String? = null
    private var userID: String? = null
    private val db = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ver_pasos_alumno)

        // Inicializar vistas
        numeroPasoTextView = findViewById(R.id.numeroPaso)
        descripcionPasoTextView = findViewById(R.id.descripcionPaso)
        imagenPasoImageView = findViewById(R.id.imagenPaso)
        btnAnteriorPaso = findViewById(R.id.btnAnteriorPaso)
        btnSiguientePaso = findViewById(R.id.btnSiguientePaso)
        btnMarcarCompletada = findViewById(R.id.btnMarcarCompletada)
        btnVolver = findViewById(R.id.btnVolver)

        // Recuperar datos del intent
        pasos = intent.getParcelableArrayListExtra("pasos", PasoTarea::class.java) ?: arrayListOf()
        tareaID = intent.getStringExtra("tareaID") ?: ""
        userID = intent.getStringExtra("userID") ?: ""

        // Ordenar los pasos por el campo `numero`
        pasos.sortBy { it.numero }

        // Verificar si hay pasos disponibles
        if (pasos.isEmpty()) {
            Toast.makeText(this, "No hay pasos disponibles.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Mostrar el paso actual
        updatePaso()

        // Configurar botones de navegación
        btnAnteriorPaso.setOnClickListener {
            if (currentPasoIndex > 0) {
                currentPasoIndex--
                updatePaso()
            }
        }

        btnSiguientePaso.setOnClickListener {
            if (currentPasoIndex < pasos.size - 1) {
                currentPasoIndex++
                updatePaso()
            }
        }

        // Marcar la tarea como completada
        btnMarcarCompletada.setOnClickListener {
            marcarTareaCompletada()
        }

        // Botón de volver
        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun updatePaso() {
        // Obtener el paso actual
        val paso = pasos[currentPasoIndex]

        // Actualizar vistas
        numeroPasoTextView.text = "Paso ${paso.numero}"
        descripcionPasoTextView.text = paso.descripcion

        // Cargar la imagen del paso usando Glide
        Glide.with(this)
            .load(paso.uri)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(imagenPasoImageView)

        // Configurar visibilidad de botones
        btnAnteriorPaso.visibility = if (currentPasoIndex > 0) Button.VISIBLE else Button.GONE
        btnSiguientePaso.visibility = if (currentPasoIndex < pasos.size - 1) Button.VISIBLE else Button.GONE

        // Mostrar el botón "Marcar Completada" solo en el último paso
        btnMarcarCompletada.visibility = if (currentPasoIndex == pasos.size - 1) Button.VISIBLE else Button.GONE
    }

    private fun marcarTareaCompletada() {
        if (tareaID.isNullOrEmpty() || userID.isNullOrEmpty()) {
            Toast.makeText(this, "Error al marcar como completada.", Toast.LENGTH_SHORT).show()
            return
        }

        // Actualizar el estado de la tarea en Firestore
        db.collection("tareas").document(tareaID!!)
            .update("asignados.$userID", true)
            .addOnSuccessListener {
                Toast.makeText(this, "Tarea marcada como completada.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, TareasAlumno::class.java)
                intent.putExtra("userID", userID)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al marcar la tarea: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
