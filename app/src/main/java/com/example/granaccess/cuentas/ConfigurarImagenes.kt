package com.example.granaccess.cuentas

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.granaccess.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ConfigurarImagenes : AppCompatActivity() {

    private val imagenes = mutableListOf<Uri?>()
    private val secuencia = mutableListOf<Int?>(null, null)

    // FIREBASE
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    // IMAGENES Y SECUENCIA
    private lateinit var configurarImagenesLauncher: ActivityResultLauncher<Intent>
    private lateinit var imageViews: List<ImageView>
    private lateinit var secuenciaViews: List<ImageView>

    private var uriCallback: ((Uri) -> Unit)? = null

    // Datos recibidos desde la vista anterior
    private lateinit var username: String
    private lateinit var password: String
    private lateinit var profileImageUri: Uri
    private var imgAndText: Boolean = false

    // AlertDialog
    private lateinit var loadingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.configurar_imagenes)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        // Inicializa las imágenes como `null`
        repeat(6) { imagenes.add(null) }

        // Referencias a los ImageViews de las imágenes principales
        imageViews = listOf(
            findViewById(R.id.ivImagen1),
            findViewById(R.id.ivImagen2),
            findViewById(R.id.ivImagen3),
            findViewById(R.id.ivImagen4),
            findViewById(R.id.ivImagen5),
            findViewById(R.id.ivImagen6)
        )

        // Referencias a los ImageViews de la secuencia
        secuenciaViews = listOf(
            findViewById(R.id.ivSecuencia1),
            findViewById(R.id.ivSecuencia2)
        )

        // Obtener datos del intent de la vista anterior
        username = intent.getStringExtra("username") ?: ""
        password = intent.getStringExtra("password") ?: ""
        profileImageUri = Uri.parse(intent.getStringExtra("iconUri"))
        imgAndText = intent.getBooleanExtra("imgAndText", false)

        // Configura el launcher para seleccionar imágenes
        configurarImagenesLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val uri = result.data!!.data
                uri?.let { uriCallback?.invoke(it) }
                uriCallback = null
            }
        }

        // Configura los ImageViews para seleccionar imágenes
        imageViews.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                seleccionarImagen { uri ->
                    imagenes[index] = uri
                    imageView.setImageURI(uri)
                }
            }
        }

        // Configura los ImageViews de la secuencia
        secuenciaViews.forEachIndexed { secIndex, secImageView ->
            secImageView.setOnClickListener {
                seleccionarIndiceDeImagen { selectedIndex ->
                    secuencia[secIndex] = selectedIndex
                    secImageView.setImageURI(imagenes[selectedIndex])
                }
            }
        }

        // Botón para guardar los datos en Firebase
        findViewById<Button>(R.id.btnGuardar).setOnClickListener {
            if (imagenes.any { it == null } || secuencia.any { it == null }) {
                Toast.makeText(this, "Completa todas las imágenes y la secuencia", Toast.LENGTH_SHORT).show()
            } else {
                showLoadingDialog()
                registrarAlumnoEnFirebase()
            }
        }

        // Botón de cancelar
        findViewById<Button>(R.id.btnCancelar).setOnClickListener {
            val intent = Intent(this, RegistroAlumno::class.java)
            startActivity(intent)
        }
    }

    // Función para seleccionar una imagen de la galería
    private fun seleccionarImagen(callback: (Uri) -> Unit) {
        uriCallback = callback
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        configurarImagenesLauncher.launch(intent)
    }

    // Función para seleccionar el índice de la imagen en la secuencia
    private fun seleccionarIndiceDeImagen(callback: (Int) -> Unit) {
        val opciones = (1..6).map { "Imagen $it" }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Selecciona una imagen para la secuencia")
            .setItems(opciones) { _, which ->
                callback(which)
            }.show()
    }

    // Función para registrar al alumno en Firebase
    private fun registrarAlumnoEnFirebase() {
        auth.createUserWithEmailAndPassword("$username@domain.com", password)
            .addOnSuccessListener {
                val userId = auth.currentUser?.uid ?: return@addOnSuccessListener

                // Subir imagen de perfil
                val profileImageRef = storage.reference.child("usuarios/alumnos/$username/foto_$username.png")
                profileImageRef.putFile(profileImageUri)
                    .addOnSuccessListener {
                        profileImageRef.downloadUrl.addOnSuccessListener { profileUrl ->
                            // Subir imágenes del patrón
                            subirImagenesPatron(username, imagenes) { imagenesUrls ->
                                // Guardar los datos en Firestore
                                guardarAlumnoEnFirestore(
                                    db, userId, username, profileUrl.toString(),
                                    imagenesUrls, secuencia.map { it!! } // Convertir a lista no nullable
                                )
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al subir la imagen de perfil: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al registrar en Firebase Auth: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Subir las 6 imágenes del patrón al Storage
    private fun subirImagenesPatron(
        username: String,
        imagenes: List<Uri?>,
        onSuccess: (List<String>) -> Unit
    ) {
        val urls = MutableList(imagenes.size) { "" } // Inicializa una lista del tamaño correcto

        imagenes.forEachIndexed { index, uri ->
            val ref = storage.reference.child("usuarios/alumnos/$username/login_${index + 1}.png")
            ref.putFile(uri!!)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener { url ->
                        urls[index] = url.toString() // Guarda en el índice correspondiente
                        if (urls.none { it.isEmpty() }) { // Verifica si todas las URLs están completas
                            onSuccess(urls)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al subir imagen ${index + 1}: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Guardar datos del alumno en Firestore
    private fun guardarAlumnoEnFirestore(
        db: FirebaseFirestore,
        userId: String,
        username: String,
        profileImageUrl: String,
        imagenesUrls: List<String>,
        secuencia: List<Int>
    ) {
        val alumnoData = mapOf(
            "username" to username,
            "imgPref" to true,
            "imgAndText" to imgAndText,
            "role" to "alumno",
            "icono" to profileImageUrl,
            "imagenes" to imagenesUrls,
            "secuencia" to secuencia
        )

        db.collection("usuarios").document(userId).set(alumnoData)
            .addOnSuccessListener {
                hideLoadingDialog()
                Toast.makeText(this, "Alumno registrado correctamente", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                var intent = Intent(this, GestionarCuentas::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar datos en Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
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
