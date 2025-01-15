package com.example.granaccess.tareas

import android.app.AlertDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.granaccess.R
import com.google.firebase.firestore.FirebaseFirestore

class VentanaVerListaMaterialIndividual : AppCompatActivity() {

    private lateinit var listViewMateriales: ListView
    private lateinit var btnAsignarAlumno: Button
    private lateinit var textAlumnoAsignado: TextView
    private val db = FirebaseFirestore.getInstance()
    private val materialesList = mutableListOf<String>() // Lista de materiales para mostrar
    private var identificadorSeleccionado: String? = null // ID de la lista seleccionada

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_lista_material_individual)

        listViewMateriales = findViewById(R.id.listViewMateriales)
        btnAsignarAlumno = findViewById(R.id.btnAsignarAlumno)
        textAlumnoAsignado = findViewById(R.id.textAlumnoAsignado)

        // Obtener el identificador pasado desde la actividad anterior
        identificadorSeleccionado = intent.getStringExtra("identificadorSeleccionado")

        if (identificadorSeleccionado != null) {
            cargarListaDesdeFirestore(identificadorSeleccionado!!)
        } else {
            Toast.makeText(this, "No se encontró la lista seleccionada", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Acción del botón "Asignar Alumno"
        btnAsignarAlumno.setOnClickListener {
            mostrarDialogoAsignarAlumno()
        }
    }

    private fun cargarListaDesdeFirestore(identificador: String) {
        db.collection("materiales")
            .whereEqualTo("identificador", identificador)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val document = result.documents[0]
                    val materialesAsignados =
                        document["materialesAsignados"] as? List<Map<String, Any>>
                    val alumnoAsignado = document.getString("alumnoasignado") ?: "No asignado"

                    textAlumnoAsignado.text = "Alumno Asignado: $alumnoAsignado"

                    materialesAsignados?.forEach { material ->
                        val materialInfo = """
                            Material: ${material["material"]}
                            Cantidad: ${material["cantidad"]}
                            Color: ${material["color"]}
                        """.trimIndent()
                        materialesList.add(materialInfo)
                    }

                    val adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_list_item_1,
                        materialesList
                    )
                    listViewMateriales.adapter = adapter
                } else {
                    Toast.makeText(this, "No se encontraron materiales en esta lista", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar lista: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mostrarDialogoAsignarAlumno() {
        val editText = EditText(this)
        editText.hint = "Ingrese el nombre del alumno"

        AlertDialog.Builder(this)
            .setTitle("Asignar Alumno")
            .setMessage("Ingrese el nombre del alumno al que desea asignar esta lista")
            .setView(editText)
            .setPositiveButton("Asignar") { _, _ ->
                val nombreAlumno = editText.text.toString().trim()
                if (nombreAlumno.isNotEmpty()) {
                    asignarAlumno(nombreAlumno)
                } else {
                    Toast.makeText(this, "El nombre del alumno no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    private fun asignarAlumno(nombreAlumno: String) {
        if (identificadorSeleccionado != null) {
            db.collection("materiales")
                .whereEqualTo("identificador", identificadorSeleccionado)
                .get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        val documentId = result.documents[0].id // Obtenemos el ID del documento
                        db.collection("materiales")
                            .document(documentId)
                            .update("alumnoasignado", nombreAlumno)
                            .addOnSuccessListener {
                                textAlumnoAsignado.text = "Alumno Asignado: $nombreAlumno" // Actualizar el texto en pantalla
                                Toast.makeText(this, "Alumno asignado correctamente", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al asignar alumno: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al buscar la lista: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Identificador no válido", Toast.LENGTH_SHORT).show()
        }
    }
}
