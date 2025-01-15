package com.example.granaccess.cuentas

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
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
    private lateinit var seleccionarImagenLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null
    private var preference: String = ""
    private var imgAndText : Boolean = false

    private var nothingSelected: Boolean = false

    private lateinit var loadingDialog: AlertDialog

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registro_alumno)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val ivIcono = findViewById<ImageView>(R.id.ivFotoPerfil)
        val switchImgPref = findViewById<Spinner>(R.id.swPrefImagen)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)

        // Registrar el launcher para seleccionar imagen
        seleccionarImagenLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                selectedImageUri = result.data!!.data
                ivIcono.setImageURI(selectedImageUri)
            }
        }

        // Selección de imagen de perfil
        ivIcono.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            seleccionarImagenLauncher.launch(intent)
        }

        // Selección de preferencia visual
        nothingSelected = true

        ArrayAdapter.createFromResource(
            this,
            R.array.imagen_preferencias,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            switchImgPref.adapter = adapter
        }

        switchImgPref.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position){
                    0 -> {
                        preference = "text"
                        nothingSelected = false
                    }

                    1 -> {
                        preference = "image"
                        nothingSelected = false
                    }

                    2 -> {
                        preference = "imagetext"
                        nothingSelected = false
                    }

                    3 -> {
                        preference = "audio"
                        nothingSelected = false
                    }
                }

                if (preference == "image" || preference == "imagetext"){
                    btnRegistrar.text = "Configurar Imágenes"
                }
                else {
                    btnRegistrar.text = "Registrar"
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                nothingSelected = true
            }
        }


        // Acción al presionar el botón de registrar
        btnRegistrar.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty() || selectedImageUri == null) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!nothingSelected) {
                if (btnRegistrar.text == "Configurar Imágenes") {
                    // Ir a la nueva vista para configurar imágenes
                    val intent = Intent(this, ConfigurarImagenes::class.java).apply {
                        putExtra("username", username)
                        putExtra("password", password)
                        putExtra("iconUri", selectedImageUri.toString())
                        putExtra("imgAndText", imgAndText)
                    }
                    startActivity(intent)
                } else {
                    // Registrar directamente al alumno en Firebase
                    if (selectedImageUri != null) {
                        registrarAlumno(username, password, selectedImageUri!!)
                    } else {
                        Toast.makeText(this, "Selecciona una imagen de perfil", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else {
                Toast.makeText(this, "Selecciona un preferencia de accesibilidad", Toast.LENGTH_SHORT).show()
            }
        }

        // Cancelar la operación
        btnCancelar.setOnClickListener {
            finish()
        }
    }

    // Método para registrar un alumno en Firebase
    private fun registrarAlumno(username: String, password: String, imageUri: Uri) {
        auth.createUserWithEmailAndPassword("$username@domain.com", password)
            .addOnSuccessListener {
                val userId = auth.currentUser?.uid ?: return@addOnSuccessListener
                subirImagenPerfil(username, imageUri) { imageUrl ->
                    guardarAlumnoEnFirestore(userId, username, imageUrl)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al registrar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Subir imagen de perfil al Firebase Storage
    private fun subirImagenPerfil(username: String, uri: Uri, onSuccess: (String) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child("usuarios/alumnos/$username/foto_$username.png")
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

    // Guardar datos del alumno en Firestore
    private fun guardarAlumnoEnFirestore(userId: String, username: String, imageUrl: String) {
        val alumno = mapOf(
            "username" to username,
            "preferencia" to preference,
            "role" to "alumno",
            "icono" to imageUrl
        )

        db.collection("usuarios").document(userId).set(alumno)
            .addOnSuccessListener {
                hideLoadingDialog()
                Toast.makeText(this, "Alumno registrado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showLoadingDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_loading, null)

        loadingDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false) // Evita que el usuario lo cierre manualmente
            .create()

        loadingDialog.show()
    }

    private fun hideLoadingDialog() {
        if (::loadingDialog.isInitialized && loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
    }
}

