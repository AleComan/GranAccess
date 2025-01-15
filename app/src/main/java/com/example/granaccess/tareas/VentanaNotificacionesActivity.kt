package com.example.granaccess.tareas

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.granaccess.R
import com.google.firebase.firestore.FirebaseFirestore

class VentanaNotificacionesActivity : AppCompatActivity() {

    private lateinit var listViewNotificaciones: ListView
    private lateinit var titulo: TextView
    private val db = FirebaseFirestore.getInstance()
    private val listaNotificaciones = mutableListOf<Notificacion>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ventana_notificaciones)

        // Configurar título y ListView
        titulo = findViewById(R.id.textTitulo)
        titulo.text = "Notificaciones"
        listViewNotificaciones = findViewById(R.id.listViewNotificaciones)

        // Cargar notificaciones desde Firebase
        cargarNotificacionesDesdeFirebase()
    }

    private fun cargarNotificacionesDesdeFirebase() {
        db.collection("notificaciones")
            .get()
            .addOnSuccessListener { result ->
                listaNotificaciones.clear()
                for (document in result) {
                    val id = document.id // Usar el ID del documento como identificador
                    val asunto = document.getString("asunto") ?: "Sin Asunto"
                    val descripcion = document.getString("descripcion") ?: "Sin Descripción"
                    val leida = document.getBoolean("leida") ?: false // Leer como Boolean
                    listaNotificaciones.add(Notificacion(id, asunto, descripcion, leida))
                }
                mostrarNotificaciones()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar las notificaciones: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mostrarNotificaciones() {
        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            listaNotificaciones.map { it.asunto }
        ) {
            override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getView(position, convertView, parent) as TextView
                val notificacion = listaNotificaciones[position]
                view.text = notificacion.asunto
                view.setTextColor(if (notificacion.leida) Color.GREEN else Color.RED)
                return view
            }
        }

        listViewNotificaciones.adapter = adapter
        listViewNotificaciones.setOnItemClickListener { _, _, position, _ ->
            val notificacionSeleccionada = listaNotificaciones[position]
            val intent = Intent(this, VentanaVerNotificacionActivity::class.java)
            intent.putExtra("notificacionId", notificacionSeleccionada.id)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        cargarNotificacionesDesdeFirebase()
    }
}
