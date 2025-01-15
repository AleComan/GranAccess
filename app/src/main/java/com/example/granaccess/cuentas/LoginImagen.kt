package com.example.granaccess.cuentas

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.granaccess.R
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class LoginImagen : AppCompatActivity() {

    private lateinit var imageViews: List<ImageView>
    private lateinit var userID: String
    private lateinit var correctSequence: List<Int>
    private lateinit var username: String
    private val selectedSequence = mutableListOf<Int>()
    private val db = FirebaseFirestore.getInstance()


    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_imagen)

        // Recuperar userID desde la actividad anterior
        userID = intent.getStringExtra("userID") ?: ""
        if (userID.isEmpty()) {
            Toast.makeText(this, "Usuario no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Recuperar username desde la actividad anterior
        username = intent.getStringExtra("username") ?: ""
        val tvSaludo = findViewById<TextView>(R.id.tvSaludo)
        tvSaludo.text = "¡Bienvenid@ $username!"

        var ivBack = findViewById<ImageView>(R.id.ivBackArrow)
        ivBack.setOnClickListener {
            finish()
        }

        // Referencias a los ImageViews
        imageViews = listOf(
            findViewById(R.id.ivImagen1),
            findViewById(R.id.ivImagen2),
            findViewById(R.id.ivImagen3),
            findViewById(R.id.ivImagen4),
            findViewById(R.id.ivImagen5),
            findViewById(R.id.ivImagen6)
        )

        // Cargar imágenes y secuencia desde Firestore
        cargarImagenesYSecuencia()

        // Configurar clics en los ImageViews
        imageViews.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                seleccionarImagen(index)
            }
        }
    }

    private fun cargarImagenesYSecuencia() {
        db.collection("usuarios").document(userID).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Recuperar URLs de las imágenes
                    val imageUrls = document.get("imagenes") as? List<String> ?: emptyList()

                    if (imageUrls.size == 6) {
                        // Cargar las imágenes en los ImageView
                        imageUrls.forEachIndexed { index, url ->
                            Picasso.get().load(url).into(imageViews[index])
                        }
                    } else {
                        Toast.makeText(this, "Error al cargar imágenes", Toast.LENGTH_SHORT).show()
                    }

                    // Recuperar la secuencia correcta y convertir a lista de enteros
                    correctSequence = (document.get("secuencia") as? List<Long>)
                        ?.map { it.toInt() } ?: emptyList()
                } else {
                    Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar datos del usuario", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    private fun seleccionarImagen(index: Int) {
        if (selectedSequence.size < 2) {
            if (index !in selectedSequence) {
                // Añadir imagen a la secuencia seleccionada
                selectedSequence.add(index)

                // Cambiar alpha para indicar selección
                imageViews[index].alpha = 0.5f

                if (selectedSequence.size == 2) {
                    // Validar la secuencia seleccionada
                    validarSecuencia()
                }
            }
        }
    }

    private fun validarSecuencia() {
        // Depuración: Imprimir secuencias para diagnóstico
        println("Secuencia seleccionada: $selectedSequence")
        println("Secuencia correcta: $correctSequence")

        // Comprobar si la secuencia seleccionada es igual a la secuencia correcta
        var isCorrect = false
        if (selectedSequence[0] == correctSequence[0] && selectedSequence[1] == correctSequence[1]) {
            isCorrect = true
        }

        if (isCorrect) {
            mostrarExito() // Mostrar el tic verde antes de cambiar de vista
        } else {
            mostrarError() // Mostrar la equis roja
        }
    }

    // Nueva función para mostrar el tic verde en caso de éxito
    private fun mostrarExito() {
        val successView = ImageView(this).apply {
            setImageResource(R.drawable.tic_verde)
            alpha = 0.9f
            layoutParams = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_PARENT,
                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_PARENT
            )
        }
        val rootLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.rootLayout)
        rootLayout.addView(successView)

        // Pasar a la vista del alumno tras 1 segundo
        Handler(Looper.getMainLooper()).postDelayed({
            rootLayout.removeView(successView)
            val intent = Intent(this, VistaAlumnoImagen::class.java)
            intent.putExtra("username", username)
            intent.putExtra("userID", userID)
            startActivity(intent)
            finish()
        }, 1000)
    }

    private fun mostrarError() {
        val errorView = ImageView(this).apply {
            setImageResource(R.drawable.ic_error_red)
            alpha = 0.9f
            layoutParams = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_PARENT,
                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_PARENT
            )
        }
        val rootLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.rootLayout)
        rootLayout.addView(errorView)

        // Reiniciar tras 1 segundo
        Handler(Looper.getMainLooper()).postDelayed({
            rootLayout.removeView(errorView)
            resetearSecuencia()
        }, 1000)
    }

    private fun resetearSecuencia() {
        selectedSequence.clear()
        imageViews.forEach { it.alpha = 1.0f }
    }
}
