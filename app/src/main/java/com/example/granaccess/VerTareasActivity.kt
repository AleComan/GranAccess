package com.example.granaccess

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class VerTareasActivity : AppCompatActivity() {

    private lateinit var listViewTareas: ListView
    private var tareasList = mutableListOf<Tarea>()
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_tareas)

        listViewTareas = findViewById(R.id.listViewTareas)

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

        // Acción al hacer una pulsación larga para modificar una tarea
        listViewTareas.setOnItemLongClickListener { _, _, position, _ ->
            val tareaSeleccionada = tareasList[position]
            val intent = Intent(this, CrearTareaActivity::class.java)
            intent.putExtra("tareaSeleccionada", gson.toJson(tareaSeleccionada))
            intent.putExtra("modoEdicion", true) // Indica que es para modificar
            startActivity(intent)
            true
        }
    }
}
