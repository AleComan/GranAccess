package com.example.granaccess.cuentas

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.granaccess.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class RegistroAlumno : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private var imagenesSeleccionadas= listOf<Uri>()
    private var secuenciaSeleccionada = listOf<Int>()
    private var isPrefImagen: Boolean = false

    private lateinit var configurarImagenesLauncher: ActivityResultLauncher<Intent>

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registro_alumno)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        val etUsuario = findViewById<EditText>(R.id.etUsuario)
        val etContrasena = findViewById<EditText>(R.id.etContrasena)
        val swPrefImagen = findViewById<Switch>(R.id.swPrefImagen)
        val btnConfigurarImagenes = findViewById<Button>(R.id.btnConfigurarImagenes)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrarAlumno)

        // Configurar preferencia
        swPrefImagen.setOnCheckedChangeListener { _, isChecked ->
            isPrefImagen = isChecked
            btnConfigurarImagenes.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        // Botón para ir a "Configurar Imágenes"
        btnConfigurarImagenes.setOnClickListener {
            val intent = Intent(this, ConfigurarImagenes::class.java)
            configurarImagenesLauncher.launch(intent)
        }

        // Configurar el ActivityResultLauncher
        configurarImagenesLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val data = result.data!!

                // Recuperar las imágenes seleccionadas
                imagenesSeleccionadas = (data.getStringArrayListExtra("imagenesSeleccionadas") ?: emptyList()).map { Uri.parse(it)}

                // Recuperar la secuencia seleccionada
                secuenciaSeleccionada = data.getIntegerArrayListExtra("secuenciaSeleccionada") ?: emptyList()

                print("Imagenes seleccionadas: $imagenesSeleccionadas\n Secuencia seleccionada: $secuenciaSeleccionada")

                Toast.makeText(
                    this,
                    "Imágenes seleccionadas: $imagenesSeleccionadas\nSecuencia seleccionada: $secuenciaSeleccionada",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Botón para registrar alumno
        btnRegistrar.setOnClickListener {

            val username = etUsuario.text.toString().trim()
            val password = etContrasena.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isPrefImagen && (imagenesSeleccionadas.isEmpty() || imagenesSeleccionadas.size < 6 || secuenciaSeleccionada.size != 2)) {
                Toast.makeText(this, "Configura las imágenes y la secuencia correctamente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registrarAlumno(username, password)
        }
    }

    private fun registrarAlumno(username: String, password: String) {
        // Crear usuario en Firebase Authentication
        auth.createUserWithEmailAndPassword("$username@example.com", password)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid ?: return@addOnSuccessListener

                if (isPrefImagen) {
                    // Subir imágenes al Storage y guardar datos en Firestore
                    subirImagenesYGuardarDatos(username, userId)
                } else {
                    // Guardar directamente los datos en Firestore
                    guardarUsuarioEnFirestore(
                        userId, username, password, isPrefImagen, listOf(), listOf()
                    )
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al registrar usuario: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun subirImagenesYGuardarDatos(username: String, userId: String) {
        val storageRef = storage.reference.child("users/$username")
        val imageUrls = mutableListOf<String>()

        imagenesSeleccionadas.forEachIndexed { index, uri ->
            val imageRef = storageRef.child("image_${index + 1}.jpg")
            imageRef.putFile(uri)
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { throw it }
                    }
                    imageRef.downloadUrl
                }
                .addOnSuccessListener { downloadUrl ->
                    imageUrls.add(downloadUrl.toString())

                    if (imageUrls.size == imagenesSeleccionadas.size) {
                        // Cuando todas las imágenes se hayan subido
                        guardarUsuarioEnFirestore(userId, username, "", isPrefImagen, imageUrls, secuenciaSeleccionada)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al subir imagen ${index + 1}: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun guardarUsuarioEnFirestore(
        userId: String,
        username: String,
        password: String,
        isPrefImagen: Boolean,
        imageUrls: List<String>,
        secuencia: List<Int>
    ) {
        val userData = mutableMapOf<String, Any>(
            "username" to username,
            "isPrefImagen" to isPrefImagen
        )

        if (isPrefImagen) {
            userData["imagenes"] = imageUrls
            userData["secuencia"] = secuencia
        }

        db.collection("usuarios").document(userId).set(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "Alumno registrado con éxito", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar en Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
