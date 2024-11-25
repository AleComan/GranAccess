package com.example.granaccess.cuentas

import android.app.AlertDialog
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

class ConfigurarImagenes : AppCompatActivity() {

    private val imagenes = mutableListOf<Uri?>()
    private val secuencia = mutableListOf<Int?>(null, null)

    private lateinit var configurarImagenesLauncher: ActivityResultLauncher<Intent>
    private lateinit var imageViews: List<ImageView>
    private lateinit var secuenciaViews: List<ImageView>

    private var uriCallback: ((Uri) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.configurar_imagenes)

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

        // Botón de guardar
        findViewById<Button>(R.id.btnGuardar).setOnClickListener {
            if (imagenes.contains(null) || secuencia.contains(null)) {
                Toast.makeText(this, "Completa todas las imágenes y la secuencia", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent().apply {
                    putStringArrayListExtra("imagenesSeleccionadas", ArrayList(imagenes.map { it.toString() }))
                    putIntegerArrayListExtra("secuenciaSeleccionada", ArrayList(secuencia.mapNotNull { it }))
                }
                setResult(RESULT_OK, intent)
                finish()
            }
        }

        // Botón de cancelar
        findViewById<Button>(R.id.btnCancelar).setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
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
}
