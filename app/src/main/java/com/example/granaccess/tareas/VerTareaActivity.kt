package com.example.granaccess.tareas

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.granaccess.R
import com.google.gson.Gson

class VerTareaActivity : AppCompatActivity() {

    private lateinit var textTituloTarea: TextView
    private lateinit var textDescripcionTarea: TextView
    private lateinit var textPasoContenido: TextView
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_tarea)

        textTituloTarea = findViewById(R.id.textTituloTarea)
        textDescripcionTarea = findViewById(R.id.textDescripcionTarea)
        textPasoContenido = findViewById(R.id.textPasoContenido)

        val tareaJson = intent.getStringExtra("tareaSeleccionada")
        if (tareaJson != null) {
            val tarea = gson.fromJson(tareaJson, Tarea::class.java)
            mostrarTarea(tarea)
        }
    }

    private fun mostrarTarea(tarea: Tarea) {
        textTituloTarea.text = tarea.titulo
        textDescripcionTarea.text = tarea.descripcion
        textPasoContenido.text = tarea.pasos.entries.joinToString("\n") { "Paso ${it.key}: ${it.value}" }
    }
}
