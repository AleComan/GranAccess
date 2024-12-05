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

data class TareaMostrar(
    val tareaID: String,
    val titulo: String,
    val descripcion: String,
    val imagenTarea: String,
    val pasos: ArrayList<PasoTarea>,
    val asignados: Map<String, Boolean>
)

class TareasAlumno : AppCompatActivity() {

    private lateinit var recyclerViewTareas: RecyclerView
    private lateinit var btnVolver: Button
    private val db = FirebaseFirestore.getInstance()
    private val tareasList = mutableListOf<TareaMostrar>()
    private lateinit var adapter: TareaAdapterAlumno

    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tareas_alumno)

        userID = intent.getStringExtra("userID") ?: ""

        // Inicializar vistas
        recyclerViewTareas = findViewById(R.id.recyclerViewTareas)
        btnVolver = findViewById(R.id.btnVolver)

        // Configurar RecyclerView
        recyclerViewTareas.layoutManager = LinearLayoutManager(this)
        adapter = TareaAdapterAlumno(tareasList, userID) { tarea ->
            verDetallesTarea(tarea)
        }
        recyclerViewTareas.adapter = adapter

        // Acción del botón "Volver"
        btnVolver.setOnClickListener {
            finish() // Regresar a la vista anterior
        }

        cargarTareas()
    }

    private fun cargarTareas() {
        db.collection("tareas").get().addOnSuccessListener { result ->
            tareasList.clear()

            for (document in result) {
                val tareaID = document.id
                val asignados = try {
                    document.get("asignados") as? Map<String, Boolean> ?: emptyMap()
                } catch (e: Exception) {
                    emptyMap<String, Boolean>() // Maneja el caso donde el formato no sea válido
                }


                // Procesar las tareas, sin importar el estado de asignados
                val tituloTarea = document.getString("titulo") ?: "Sin título"
                val descripcionTarea = document.getString("desc") ?: "Sin descripción"
                val imagenTarea = document.getString("img") ?: ""
                val pasosFirestore = document.get("pasos") as? List<Map<String, Any>>

                val pasos = ArrayList<PasoTarea>()
                pasosFirestore?.forEach { paso ->
                    val numero = (paso["num"] as? Number)?.toInt() ?: 0
                    val descripcion = paso["desc_paso"] as? String ?: ""
                    val imagen = paso["img_paso"] as? String ?: ""
                    pasos.add(PasoTarea(numero, descripcion, imagen))
                }

                // Agregar tarea al RecyclerView
                tareasList.add(
                    TareaMostrar(
                        tareaID = tareaID,
                        titulo = tituloTarea,
                        descripcion = descripcionTarea,
                        imagenTarea = imagenTarea,
                        pasos = pasos,
                        asignados = asignados
                    )
                )
            }

            adapter.notifyDataSetChanged()

            if (tareasList.isEmpty()) {
                Toast.makeText(this, "No hay tareas disponibles para este alumno.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Error al cargar tareas: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun verDetallesTarea(tarea: TareaMostrar) {
        val intent = Intent(this, VerTareaAlumno::class.java)
        intent.putExtra("tareaID", tarea.tareaID)
        intent.putExtra("userID", userID)
        intent.putExtra("titulo", tarea.titulo)
        intent.putExtra("descripcion", tarea.descripcion)
        intent.putExtra("imagenTarea", tarea.imagenTarea)
        intent.putParcelableArrayListExtra("pasos", tarea.pasos)
        startActivity(intent)
    }
}
