package com.example.granaccess.tareas

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.granaccess.R
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

data class Alumno(val id: String, val username: String, val icono: String)

class SeleccionAlumnosTarea : AppCompatActivity() {

    private lateinit var saveTaskButton: Button
    private lateinit var alumnosListContainer: LinearLayout

    private var titulo: String? = null
    private var descripcion: String? = null
    private var imageUri: String? = null
    private var pasos: ArrayList<PasoTarea>? = null
    private val alumnosSeleccionados = mutableMapOf<String, Boolean>()

    private val firestore by lazy { FirebaseFirestore.getInstance() }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.seleccion_alumnos_tarea)

        saveTaskButton = findViewById(R.id.saveTaskButton)
        alumnosListContainer = findViewById(R.id.alumnosListContainer)

        // Recibir datos de la tarea
        titulo = intent.getStringExtra("titulo")
        descripcion = intent.getStringExtra("descripcion")
        imageUri = intent.getStringExtra("imageUri")
        pasos = intent.getParcelableArrayListExtra("pasos", PasoTarea::class.java)

        if (titulo.isNullOrEmpty() || descripcion.isNullOrEmpty() || imageUri.isNullOrEmpty() || pasos.isNullOrEmpty()) {
            Toast.makeText(this, "Error al recibir los datos de la tarea.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Recuperar la lista de alumnos desde Firebase
        fetchAlumnos()

        // Guardar la tarea
        saveTaskButton.setOnClickListener {
            if (alumnosSeleccionados.isEmpty()) {
                Toast.makeText(this, "Selecciona al menos un alumno.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            saveTaskToFirebase()
            var intent = Intent(this, GestionTareasActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchAlumnos() {
        firestore.collection("usuarios")
            .whereEqualTo("role", "alumno") // Filtrar por el rol "alumno"
            .get()
            .addOnSuccessListener { querySnapshot ->
                val alumnos = querySnapshot.documents.mapNotNull { document ->
                    val id = document.id
                    val username = document.getString("username")
                    val icono = document.getString("icono")
                    if (username != null && icono != null) {
                        Alumno(id, username, icono)
                    } else {
                        null
                    }
                }

                populateAlumnosList(alumnos)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar alumnos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun populateAlumnosList(alumnos: List<Alumno>) {
        for (alumno in alumnos) {
            val alumnoView = layoutInflater.inflate(R.layout.item_usuario_checkbox, alumnosListContainer, false)
            val alumnoNameTextView = alumnoView.findViewById<TextView>(R.id.alumnoUsername)
            val alumnoIconImageView = alumnoView.findViewById<ImageView>(R.id.alumnoIcon)
            val alumnoCheckBox = alumnoView.findViewById<CheckBox>(R.id.alumnoCheckBox)

            alumnoNameTextView.text = alumno.username
            Glide.with(this).load(alumno.icono).into(alumnoIconImageView) // Cargar imagen desde URL

            alumnoCheckBox.setOnCheckedChangeListener { _, isChecked ->
                alumnosSeleccionados[alumno.id] = isChecked
                if (!isChecked) {
                    alumnosSeleccionados.remove(alumno.id)
                }
            }

            alumnosListContainer.addView(alumnoView)
        }
    }

    private fun saveTaskToFirebase() {
        val tareaId = UUID.randomUUID().toString()
        val tareaRef = firestore.collection("tareas").document(tareaId)
        val storageRef = FirebaseStorage.getInstance().reference.child("tareas/$tareaId")

        // Subir imagen general
        val generalImageRef = storageRef.child("tarea_general")
        val generalImageUploadTask = generalImageRef.putFile(Uri.parse(imageUri!!))

        generalImageUploadTask.addOnSuccessListener { generalImageSnapshot ->
            generalImageRef.downloadUrl.addOnSuccessListener { generalImageUrl ->
                // Subir imágenes de los pasos
                val pasosData = mutableListOf<Map<String, Any>>()
                val uploadTasks = mutableListOf<Task<Uri>>() // Lista para almacenar las tareas de subida de imágenes

                pasos!!.forEach { paso ->
                    val pasoImageRef = storageRef.child("paso_${paso.numero}")
                    val pasoUploadTask = pasoImageRef.putFile(Uri.parse(paso.uri))
                    val downloadUrlTask = pasoUploadTask.continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let { throw it }
                        }
                        pasoImageRef.downloadUrl
                    }
                    uploadTasks.add(downloadUrlTask)

                    downloadUrlTask.addOnSuccessListener { pasoImageUrl ->
                        pasosData.add(
                            mapOf(
                                "num" to paso.numero,
                                "desc_paso" to paso.descripcion,
                                "img_paso" to pasoImageUrl.toString()
                            )
                        )
                    }
                }

                // Esperar a que todas las imágenes de los pasos se suban
                Tasks.whenAllSuccess<Uri>(uploadTasks).addOnSuccessListener {
                    // Crear el mapa de asignación de alumnos
                    val asignadosMap = alumnosSeleccionados.mapValues { false }

                    // Guardar la tarea en Firestore
                    val tareaData = mapOf(
                        "titulo" to titulo,
                        "desc" to descripcion,
                        "img" to generalImageUrl.toString(),
                        "asignados" to asignadosMap,
                        "pasos" to pasosData
                    )

                    tareaRef.set(tareaData).addOnSuccessListener {
                        Toast.makeText(this, "Tarea guardada con éxito.", Toast.LENGTH_SHORT).show()
                        finish()
                    }.addOnFailureListener { e ->
                        Toast.makeText(this, "Error al guardar la tarea: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Error al subir imágenes de los pasos: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener URL de la imagen general: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error al subir la imagen general: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

}
