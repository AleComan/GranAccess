package com.example.granaccess.tareas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.granaccess.R
import com.google.firebase.firestore.FirebaseFirestore

class ListaModificarActivity : AppCompatActivity() {

    private lateinit var recyclerViewTareas: RecyclerView
    private lateinit var backButton: Button
    private val db = FirebaseFirestore.getInstance()
    private val tareasList = mutableListOf<TareaMostrar>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_tareas)

        recyclerViewTareas = findViewById(R.id.recyclerViewTareas)
        recyclerViewTareas.layoutManager = LinearLayoutManager(this)
        backButton = findViewById(R.id.backButton)

        // Configurar el adaptador
        val adapter = TareaAdapterModificar(tareasList) { tarea ->
            irAModificarTarea(tarea)
        }
        recyclerViewTareas.adapter = adapter

        // Cargar tareas desde Firestore
        cargarTareasDesdeFirestore(adapter)

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun cargarTareasDesdeFirestore(adapter: TareaAdapterModificar) {
        db.collection("tareas")
            .get()
            .addOnSuccessListener { result ->
                tareasList.clear()
                for (document in result) {
                    val tareaID = document.id
                    val titulo = document.getString("titulo") ?: "Sin título"
                    val descripcion = document.getString("descripcion") ?: "Sin descripción"
                    val asignados = document.get("asignados") as? Map<String, Boolean> ?: emptyMap()

                    // Crear una instancia de TareaMostrar y añadirla a la lista
                    tareasList.add(TareaMostrar(tareaID, titulo, descripcion, "", arrayListOf(), asignados))
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar tareas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun irAModificarTarea(tarea: TareaMostrar) {
        val intent = Intent(this, OpcionesModificarTarea::class.java)
        intent.putExtra("tareaID", tarea.tareaID)
        intent.putExtra("titulo", tarea.titulo)
        intent.putExtra("descripcion", tarea.descripcion)
        startActivity(intent)
    }
}
