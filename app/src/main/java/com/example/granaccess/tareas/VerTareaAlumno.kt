package com.example.granaccess.tareas

import android.content.Intent
import android.net.Uri
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

class VerTareaAlumno : AppCompatActivity() {

    private lateinit var tituloTareaTextView: TextView
    private lateinit var descripcionTareaTextView: TextView
    private lateinit var imagenTareaImageView: ImageView
    private lateinit var btnVerPasos: Button
    private lateinit var btnVolver: Button

    private var tareaID: String? = null
    private var titulo: String? = null
    private var descripcion: String? = null
    private var imagenTarea: String? = null
    private var pasos: ArrayList<PasoTarea>? = null

    private var userID: String? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ver_tarea_alumno)

        // Inicializar vistas
        tituloTareaTextView = findViewById(R.id.tituloTarea)
        descripcionTareaTextView = findViewById(R.id.descripcionTarea)
        imagenTareaImageView = findViewById(R.id.imagenTarea)
        btnVerPasos = findViewById(R.id.btnVerPasos)
        btnVolver = findViewById(R.id.btnVolver)

        // Recuperar datos pasados desde el intent
        tareaID = intent.getStringExtra("tareaID") ?: ""
        userID = intent.getStringExtra("userID") ?: ""
        titulo = intent.getStringExtra("titulo") ?: ""
        descripcion = intent.getStringExtra("descripcion") ?: ""
        imagenTarea = intent.getStringExtra("imagenTarea") ?: ""
        pasos = intent.getParcelableArrayListExtra("pasos", PasoTarea::class.java) ?: arrayListOf()

        // Mostrar los datos en las vistas
        tituloTareaTextView.text = titulo ?: "Sin título"
        descripcionTareaTextView.text = descripcion ?: "Sin descripción"

        // Cargar la imagen usando Glide
        if (!imagenTarea.isNullOrEmpty()) {
            Glide.with(this)
                .load(imagenTarea)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(imagenTareaImageView)
        } else {
            imagenTareaImageView.setImageResource(R.drawable.placeholder)
        }

        // Botón para ver los pasos
        btnVerPasos.setOnClickListener {
            if (pasos.isNullOrEmpty()) {
                // Si no hay pasos, mostramos un mensaje
                Toast.makeText(this, "No hay pasos disponibles para esta tarea.", Toast.LENGTH_SHORT).show()
            } else {
                // Redirigir a una actividad que muestre los pasos
                val intent = Intent(this, VerPasosAlumno::class.java)
                intent.putParcelableArrayListExtra("pasos", pasos)
                intent.putExtra("userID", userID)
                intent.putExtra("tareaID", tareaID)
                startActivity(intent)
            }
        }

        // Botón para volver
        btnVolver.setOnClickListener {
            finish() // Cerrar esta actividad y regresar
        }
    }
}
