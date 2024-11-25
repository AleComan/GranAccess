package com.example.granaccess.tareas

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.granaccess.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListaVerTareasActivity : AppCompatActivity() {

    private lateinit var listViewTareas: ListView
    private lateinit var textTituloLista: TextView
    private var tareasList = mutableListOf<Tarea>()
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_tareas)

        listViewTareas = findViewById(R.id.listViewTareas)
        textTituloLista = findViewById(R.id.textTituloLista)

        // Configurar el título
        textTituloLista.text = "Ver Tareas"

        // Recuperar las tareas guardadas
        val sharedPrefs = getSharedPreferences("TareasPref", MODE_PRIVATE)
        val json = sharedPrefs.getString("TAREAS_KEY", null)
        if (json != null) {
            val type = object : TypeToken<MutableList<Tarea>>() {}.type
            tareasList = gson.fromJson(json, type)
        }

        // Mostrar los títulos de las tareas en el ListView
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, tareasList.map { it.titulo })
        listViewTareas.adapter = adapter

        // Acción al seleccionar una tarea para ver
        listViewTareas.setOnItemClickListener { _, _, position, _ ->
            val tareaSeleccionada = tareasList[position]
            val intent = Intent(this, VerTareaActivity::class.java)
            intent.putExtra("tareaSeleccionada", gson.toJson(tareaSeleccionada))
            startActivity(intent)
        }
    }
}
