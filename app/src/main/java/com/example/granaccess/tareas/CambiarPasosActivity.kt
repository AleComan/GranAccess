package com.example.granaccess.tareas

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.granaccess.R
import com.google.firebase.firestore.FirebaseFirestore

class CambiarPasosActivity : AppCompatActivity() {

    private lateinit var recyclerViewPasos: RecyclerView
    private lateinit var btnGuardarCambios: Button
    private lateinit var btnVolver: Button

    private val pasos = mutableListOf<PasoTarea>()
    private lateinit var adapter: PasoTareaAdapter
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private var tareaID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cambiar_orden_pasos)

        recyclerViewPasos = findViewById(R.id.recyclerViewPasos)
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios)
        btnVolver = findViewById(R.id.btnVolver)

        tareaID = intent.getStringExtra("tareaID")

        if (tareaID.isNullOrEmpty()) {
            Toast.makeText(this, "No se encontró el ID de la tarea", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupRecyclerView()
        loadSteps()

        btnVolver.setOnClickListener {
            finish()
        }

        btnGuardarCambios.setOnClickListener {
            saveStepsToFirebase()
        }


    }

    private fun setupRecyclerView() {
        adapter = PasoTareaAdapter(pasos, this)
        recyclerViewPasos.layoutManager = LinearLayoutManager(this)
        recyclerViewPasos.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                return makeMovementFlags(dragFlags, 0)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                adapter.moveItem(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // No se requiere acción en el swipe
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerViewPasos)
    }

    private fun loadSteps() {
        firestore.collection("tareas").document(tareaID!!)
            .get()
            .addOnSuccessListener { document ->
                val pasosFirestore = document.get("pasos") as? List<Map<String, Any>> ?: emptyList()
                pasos.clear()
                pasos.addAll(
                    pasosFirestore.map { paso ->
                        PasoTarea(
                            numero = (paso["num"] as Long).toInt(),
                            descripcion = paso["desc_paso"] as String,
                            uri = paso["img_paso"] as String
                        )
                    }.sortedBy { it.numero }
                )
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar pasos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveStepsToFirebase() {
        pasos.forEachIndexed { index, paso ->
            paso.numero = index + 1 // Actualiza el número según el nuevo orden
        }

        val updatedSteps = pasos.map { paso ->
            mapOf(
                "num" to paso.numero,
                "desc_paso" to paso.descripcion,
                "img_paso" to paso.uri
            )
        }

        firestore.collection("tareas").document(tareaID!!)
            .update("pasos", updatedSteps)
            .addOnSuccessListener {
                Toast.makeText(this, "Pasos actualizados con éxito.", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar cambios: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
