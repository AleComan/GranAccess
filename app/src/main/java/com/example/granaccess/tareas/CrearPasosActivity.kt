package com.example.granaccess.tareas

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.granaccess.R

class CrearPasosActivity : AppCompatActivity() {

    private lateinit var editPasoContenido: EditText
    private lateinit var ivImagenPaso: ImageView
    private lateinit var btnGuardarTarea: Button
    private lateinit var btnPasoAnterior: Button
    private lateinit var btnPasoSiguiente: Button
    private lateinit var textPaso: TextView

    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private var pasos = mutableListOf<PasoTarea>()
    private var currentPasoIndex = 0
    private var selectedImageUri: Uri? = null
    private var currentPasoNumber = 1

    private var titulo: String? = null
    private var descripcion: String? = null
    private var imageUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_tarea_2)

        editPasoContenido = findViewById(R.id.editPasoContenido)
        ivImagenPaso = findViewById(R.id.ivImagenPaso)
        btnGuardarTarea = findViewById(R.id.btnGuardarTarea)
        btnPasoAnterior = findViewById(R.id.btnPasoAnterior)
        btnPasoSiguiente = findViewById(R.id.btnPasoSiguiente)
        textPaso = findViewById(R.id.textPaso)

        // Recibir información de la tarea general desde CrearTareaActivity
        titulo = intent.getStringExtra("titulo")
        descripcion = intent.getStringExtra("descripcion")
        imageUri = intent.getStringExtra("imageUri")

        // Verificar que la información general esté presente
        if (titulo.isNullOrEmpty() || descripcion.isNullOrEmpty() || imageUri.isNullOrEmpty()) {
            Toast.makeText(this, "Error al recibir datos de la tarea.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Inicializar el lanzador de selección de imágenes
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                ivImagenPaso.setImageURI(uri)
            }
        }

        // Configuración inicial
        pasos.add(PasoTarea(1, "", ""))
        updatePasoView()

        // Manejar selección de imagen
        ivImagenPaso.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Botón siguiente paso
        btnPasoSiguiente.setOnClickListener {
            if (!validateCurrentPaso()) return@setOnClickListener

            saveCurrentPaso()
            currentPasoIndex++
            if (currentPasoIndex >= pasos.size) {
                currentPasoNumber++
                pasos.add(PasoTarea(currentPasoNumber, "", ""))
            }
            updatePasoView()
        }

        // Botón paso anterior
        btnPasoAnterior.setOnClickListener {
            if (currentPasoIndex > 0) {
                saveCurrentPaso()
                currentPasoIndex--
                updatePasoView()
            }
        }

        // Botón guardar tarea
        btnGuardarTarea.setOnClickListener {
            if (!validateCurrentPaso()) return@setOnClickListener

            saveCurrentPaso()
            val intent = Intent(this, SeleccionAlumnosTarea::class.java)
            intent.putExtra("titulo", titulo)
            intent.putExtra("descripcion", descripcion)
            intent.putExtra("imageUri", imageUri)
            intent.putParcelableArrayListExtra("pasos", ArrayList(pasos)) // Lista de pasos serializable
            startActivity(intent)
        }
    }

    private fun validateCurrentPaso(): Boolean {
        val descripcion = editPasoContenido.text.toString()
        if (descripcion.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Completa los campos del paso antes de continuar.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun saveCurrentPaso() {
        val descripcion = editPasoContenido.text.toString()
        pasos[currentPasoIndex] = PasoTarea(currentPasoIndex + 1, descripcion, selectedImageUri.toString())
    }

    private fun updatePasoView() {
        val paso = pasos[currentPasoIndex]
        textPaso.text = "Paso ${paso.numero}"
        editPasoContenido.setText(paso.descripcion)
        selectedImageUri = if (paso.uri.isNotEmpty()) Uri.parse(paso.uri) else null
        ivImagenPaso.setImageURI(selectedImageUri ?: null)

        btnPasoAnterior.isEnabled = currentPasoIndex > 0
    }

    private fun pickImage() {
        pickImageLauncher.launch("image/*")
    }
}
