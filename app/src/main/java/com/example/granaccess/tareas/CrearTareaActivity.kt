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
import com.example.granaccess.R

class CrearTareaActivity : AppCompatActivity() {

    private lateinit var tituloEditText: EditText
    private lateinit var descripcionEditText: EditText
    private lateinit var tareaImageView: ImageView
    private lateinit var btnPasos: Button
    private lateinit var btnVolver: Button
    private var selectedImageUri: Uri? = null

    // ActivityResultLauncher para seleccionar imágenes
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_tarea_1)

        tituloEditText = findViewById(R.id.editTituloTarea)
        descripcionEditText = findViewById(R.id.editDescripcionTarea)
        tareaImageView = findViewById(R.id.ivTarea)
        btnPasos = findViewById(R.id.btnPasos)
        btnVolver = findViewById(R.id.btnVolver)

        // Inicializar el lanzador de selección de imágenes
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                tareaImageView.setImageURI(uri)
            }
        }

        // Abrir selector de imágenes al hacer clic en la imagen
        tareaImageView.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Navegar a la actividad de creación de pasos
        btnPasos.setOnClickListener {
            val titulo = tituloEditText.text.toString()
            val descripcion = descripcionEditText.text.toString()

            if (titulo.isEmpty() || descripcion.isEmpty() || selectedImageUri == null) {
                Toast.makeText(this, "Por favor, completa todos los campos antes de continuar.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Enviar datos a la actividad CrearPasosActivity
            val intent = Intent(this, CrearPasosActivity::class.java)
            intent.putExtra("titulo", titulo)
            intent.putExtra("descripcion", descripcion)
            intent.putExtra("imageUri", selectedImageUri.toString())
            startActivity(intent)
        }

        // Volver a la actividad anterior
        btnVolver.setOnClickListener {
            finish()
        }
    }
}
