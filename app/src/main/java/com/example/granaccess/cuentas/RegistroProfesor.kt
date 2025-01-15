package com.example.granaccess.cuentas

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.granaccess.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class RegistroProfesor : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private lateinit var seleccionarImagenLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registro_profesor)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val ivFotoPerfil = findViewById<ImageView>(R.id.ivFotoPerfil)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrarProfesor)
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)

        // Registrar el launcher para seleccionar imagen
        seleccionarImagenLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                selectedImageUri = result.data!!.data
                ivFotoPerfil.setImageURI(selectedImageUri)
            }
        }

        // Botón para seleccionar imagen
        ivFotoPerfil.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            seleccionarImagenLauncher.launch(intent)
        }

        // Botón para registrar profesor
        btnRegistrar.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                if (selectedImageUri != null) {
                    registrarProfesor(username, password, selectedImageUri!!)
                } else {
                    Toast.makeText(this, "Por favor selecciona una imagen", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón para cancelar
        btnCancelar.setOnClickListener {
            finish()
        }
    }

    // Método para registrar profesor
    private fun registrarProfesor(username: String, password: String, imageUri: Uri) {
        auth.createUserWithEmailAndPassword("$username@domain.com", password)
            .addOnSuccessListener {
                val userId = auth.currentUser?.uid ?: return@addOnSuccessListener
                subirImagen(username, imageUri) { imageUrl ->
                    guardarProfesorEnFirestore(userId, username, imageUrl)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al registrar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Subir imagen a Firebase Storage
    private fun subirImagen(username: String, uri: Uri, onSuccess: (String) -> Unit) {
        val storageRef = storage.reference.child("usuarios/profesores/$username/foto_$username.png")
        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { url ->
                    onSuccess(url.toString())
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al subir imagen: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Guardar datos del profesor en Firestore
    private fun guardarProfesorEnFirestore(userId: String, username: String, imageUrl: String) {
        val profesor = mapOf(
            "username" to username,
            "role" to "profesor",
            "imgPref" to false,
            "icono" to imageUrl
        )

        db.collection("usuarios").document(userId).set(profesor)
            .addOnSuccessListener {
                Toast.makeText(this, "Profesor registrado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
