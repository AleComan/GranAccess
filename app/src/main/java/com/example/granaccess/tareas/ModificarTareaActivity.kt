package com.example.granaccess.tareas

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.granaccess.R
import com.google.firebase.firestore.FirebaseFirestore

class ModificarTareaActivity : AppCompatActivity() {

    private lateinit var editTituloTarea: EditText
    private lateinit var editDescripcionTarea: EditText
    private lateinit var ivTarea: ImageView
    private lateinit var btnVolver: Button
    private lateinit var btnModificarPasos: Button

    private val db = FirebaseFirestore.getInstance()

    private var tareaID: String? = null
    private var tituloOriginal: String? = null
    private var descripcionOriginal: String? = null
    private var imagenOriginal: String? = null
    private var asignados: ArrayList<asignadosParcelable> = arrayListOf()

    private var pasosOriginales: ArrayList<PasoTarea> = arrayListOf() // Lista para los pasos de la tarea
    private var imagenSeleccionadaUri: Uri? = null // Para manejar cambios locales en la imagen

    // ActivityResultLauncher para manejar la selección de imágenes
    private val seleccionarImagenLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                imagenSeleccionadaUri = uri
                ivTarea.setImageURI(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificar_tarea)

        // Inicializar vistas
        editTituloTarea = findViewById(R.id.editTituloTarea)
        editDescripcionTarea = findViewById(R.id.editDescripcionTarea)
        ivTarea = findViewById(R.id.ivTarea)
        btnVolver = findViewById(R.id.btnVolver)
        btnModificarPasos = findViewById(R.id.btnPasos)

        // Recuperar tareaID del intent
        tareaID = intent.getStringExtra("tareaID")

        if (tareaID.isNullOrEmpty()) {
            Toast.makeText(this, "No se pudo cargar la tarea.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Cargar datos de la tarea desde Firestore
        cargarTareaDesdeFirestore()

        // Configurar clic en la imagen para cambiarla
        ivTarea.setOnClickListener {
            seleccionarImagen()
        }

        // Configurar clic en "Volver"
        btnVolver.setOnClickListener {
            finish()
        }

        // Configurar clic en "Modificar Pasos"
        btnModificarPasos.setOnClickListener {
            abrirModificarPasos()
        }
    }

    private fun cargarTareaDesdeFirestore() {
        db.collection("tareas").document(tareaID!!)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    tituloOriginal = document.getString("titulo")
                    descripcionOriginal = document.getString("desc")
                    imagenOriginal = document.getString("img")
                    asignados = document.get("asignados") as? ArrayList<asignadosParcelable> ?: arrayListOf()

                    val pasosFirestore = document.get("pasos") as? List<Map<String, Any>> ?: listOf()

                    // Convertir los pasos de Firestore a PasoTarea
                    pasosOriginales.clear()
                    for (paso in pasosFirestore) {
                        val numero = (paso["num"] as? Number)?.toInt() ?: 0
                        val descripcion = paso["desc_paso"] as? String ?: ""
                        val imagen = paso["img_paso"] as? String ?: ""
                        pasosOriginales.add(PasoTarea(numero, descripcion, imagen))
                    }

                    // Mostrar datos originales
                    editTituloTarea.setText(tituloOriginal)
                    editDescripcionTarea.setText(descripcionOriginal)

                    if (!imagenOriginal.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(imagenOriginal)
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .into(ivTarea)
                    } else {
                        ivTarea.setImageResource(R.drawable.placeholder)
                    }
                } else {
                    Toast.makeText(this, "La tarea no existe.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar la tarea: ${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    private fun seleccionarImagen() {
        seleccionarImagenLauncher.launch("image/*")
    }

    private fun abrirModificarPasos() {
        val tituloActual = editTituloTarea.text.toString().trim()
        val descripcionActual = editDescripcionTarea.text.toString().trim()

        val tituloParaPasos = if (tituloActual != tituloOriginal) tituloActual else null
        val descripcionParaPasos = if (descripcionActual != descripcionOriginal) descripcionActual else null
        val imagenParaPasos = imagenSeleccionadaUri?.toString() ?: imagenOriginal

        val intent = Intent(this, ModificarPasosActivity::class.java)
        intent.putExtra("tareaID", tareaID)
        intent.putExtra("titulo", tituloParaPasos ?: tituloOriginal)
        intent.putExtra("descripcion", descripcionParaPasos ?: descripcionOriginal)
        intent.putExtra("imagen", imagenParaPasos)
        intent.putParcelableArrayListExtra("pasos", pasosOriginales) // Enviar pasos a la siguiente actividad
        intent.putParcelableArrayListExtra("asignados", asignados)
        startActivity(intent)
    }
}
