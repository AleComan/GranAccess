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

class ModificarAlumnosActivity : AppCompatActivity() {

    private lateinit var saveTaskButton: Button
    private lateinit var alumnosListContainer: LinearLayout

    private var tareaID: String? = null
    private var titulo: String? = null
    private var descripcion: String? = null
    private var imagen: String? = null
    private var pasos: ArrayList<PasoTarea>? = null
    private var asignados: MutableMap<String, Boolean> = mutableMapOf()
    private var asignadosNuevo: MutableMap<String, Boolean> = mutableMapOf()

    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val storage by lazy { FirebaseStorage.getInstance().reference }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.seleccion_alumnos_tarea)

        saveTaskButton = findViewById(R.id.saveTaskButton)
        alumnosListContainer = findViewById(R.id.alumnosListContainer)

        // Retrieve task details
        tareaID = intent.getStringExtra("tareaID")
        titulo = intent.getStringExtra("titulo")
        descripcion = intent.getStringExtra("descripcion")
        imagen = intent.getStringExtra("imagen")
        pasos = intent.getParcelableArrayListExtra("pasos", PasoTarea::class.java)

        fetchAsignados()

        saveTaskButton.setOnClickListener {
            saveTaskToFirebase()
        }
    }

    private fun fetchAsignados() {
        firestore.collection("tareas").document(tareaID!!)
            .get()
            .addOnSuccessListener { document ->
                @Suppress("UNCHECKED_CAST")
                asignados = document.get("asignados") as? MutableMap<String, Boolean> ?: mutableMapOf()
                asignadosNuevo = asignados.toMutableMap()
                fetchAlumnos()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar asignados: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchAlumnos() {
        firestore.collection("usuarios")
            .whereEqualTo("role", "alumno")
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
            Glide.with(this).load(alumno.icono).into(alumnoIconImageView)

            alumnoCheckBox.isChecked = asignados.containsKey(alumno.id)

            alumnoCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (asignados.containsKey(alumno.id)) {
                        asignadosNuevo[alumno.id] = asignados[alumno.id]!!
                    } else {
                        asignadosNuevo[alumno.id] = false
                    }
                } else {
                    asignadosNuevo.remove(alumno.id)
                }
            }

            alumnosListContainer.addView(alumnoView)
        }
    }

    private fun saveTaskToFirebase() {
        val tareaRef = firestore.collection("tareas").document(tareaID!!)
        val taskFolderRef = storage.child("tareas/$tareaID")

        // Upload general image if updated
        val imageUploadTask = if (!imagen.isNullOrEmpty() && imagen!!.startsWith("content://")) {
            val generalImageRef = taskFolderRef.child("tarea_general")
            generalImageRef.putFile(Uri.parse(imagen!!))
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { throw it }
                    }
                    generalImageRef.downloadUrl
                }.addOnSuccessListener { downloadUrl ->
                    imagen = downloadUrl.toString() // Update imagen with Firebase Storage URL
                }
        } else {
            null
        }

        // Upload step images if updated
        val stepUploadTasks = pasos?.mapNotNull { paso ->
            if (paso.uri.startsWith("content://")) {
                val pasoImageRef = taskFolderRef.child("paso_${paso.numero}")
                pasoImageRef.putFile(Uri.parse(paso.uri))
                    .continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let { throw it }
                        }
                        pasoImageRef.downloadUrl
                    }.addOnSuccessListener { downloadUrl ->
                        paso.uri = downloadUrl.toString() // Update URI to Firebase Storage URL
                    }
            } else {
                null
            }
        } ?: emptyList()

        // Wait for all uploads to complete
        val allUploads = mutableListOf<Task<Uri>>()
        imageUploadTask?.let { allUploads.add(it) }
        allUploads.addAll(stepUploadTasks)

        Tasks.whenAllComplete(allUploads).addOnSuccessListener {
            // Prepare data for Firestore
            val tareaData = mutableMapOf<String, Any>()
            if (!titulo.isNullOrEmpty()) tareaData["titulo"] = titulo!!
            if (!descripcion.isNullOrEmpty()) tareaData["desc"] = descripcion!!
            if (!imagen.isNullOrEmpty()) tareaData["img"] = imagen!! // Use updated URL

            val pasosData = pasos?.map { paso ->
                mapOf(
                    "num" to paso.numero,
                    "desc_paso" to paso.descripcion,
                    "img_paso" to paso.uri
                )
            } ?: emptyList()
            if (pasosData.isNotEmpty()) {
                tareaData["pasos"] = pasosData
            }

            // Add updated asignados
            tareaData["asignados"] = asignadosNuevo

            tareaRef.update(tareaData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Cambios guardados con éxito.", Toast.LENGTH_SHORT).show()
                    var intent = Intent(this, ListaModificarActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al guardar cambios: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error al subir imágenes: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

}
