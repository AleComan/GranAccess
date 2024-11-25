package com.example.granaccess.tareas

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.granaccess.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class VentanaNotificacionesActivity : AppCompatActivity() {

    private lateinit var listViewNotificaciones: ListView
    private val gson = Gson()
    private val notificacionesKey = "NOTIFICACIONES_KEY"
    private var listaNotificaciones = mutableListOf<Notificacion>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ventana_notificaciones)

        val titulo = findViewById<TextView>(R.id.textTitulo)
        titulo.text = "Notificaciones"

        listViewNotificaciones = findViewById(R.id.listViewNotificaciones)

        cargarNotificaciones()
        mostrarNotificaciones()
    }

    private fun cargarNotificaciones() {
        val sharedPrefs = getSharedPreferences("NotificacionesPref", MODE_PRIVATE)
        val json = sharedPrefs.getString(notificacionesKey, null)
        val type = object : TypeToken<MutableList<Notificacion>>() {}.type
        listaNotificaciones = if (json != null) {
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }
    }

    private fun mostrarNotificaciones() {
        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaNotificaciones.map { it.asunto }) {
            override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getView(position, convertView, parent) as TextView
                val notificacion = listaNotificaciones[position]
                view.setTextColor(if (notificacion.leida) Color.GREEN else Color.RED)
                return view
            }
        }
        listViewNotificaciones.adapter = adapter

        listViewNotificaciones.setOnItemClickListener { _, _, position, _ ->
            val notificacionSeleccionada = listaNotificaciones[position]
            val intent = Intent(this, VentanaVerNotificacionActivity::class.java)
            intent.putExtra("notificacionSeleccionada", gson.toJson(notificacionSeleccionada))
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        cargarNotificaciones()
        mostrarNotificaciones()
    }
}
