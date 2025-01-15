package com.example.granaccess.tareas

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.granaccess.R
import com.google.firebase.firestore.FirebaseFirestore

class CrearNotificacionActivity : AppCompatActivity() {

    private lateinit var editAsuntoNotificacion: EditText
    private lateinit var editDescripcionNotificacion: EditText
    private lateinit var btnCancelar: Button
    private lateinit var btnEnviarNotificacion: Button

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_notificacion)

        // Inicializar vistas
        editAsuntoNotificacion = findViewById(R.id.editAsuntoNotificacion)
        editDescripcionNotificacion = findViewById(R.id.editDescripcionNotificacion)
        btnCancelar = findViewById(R.id.btnCancelar)
        btnEnviarNotificacion = findViewById(R.id.btnEnviarNotificacion)

        // Lógica de botones
        btnCancelar.setOnClickListener {
            finish()
        }

        btnEnviarNotificacion.setOnClickListener {
            guardarNotificacionEnFirestore()
        }
    }

    private fun guardarNotificacionEnFirestore() {
        val asunto = editAsuntoNotificacion.text.toString().trim()
        val descripcion = editDescripcionNotificacion.text.toString().trim()

        if (asunto.isNotEmpty() && descripcion.isNotEmpty()) {
            // Crear el objeto de notificación
            val notificacion = mapOf(
                "asunto" to asunto,
                "descripcion" to descripcion,
                "leida" to false // Por defecto, la notificación no está leída
            )

            // Guardar en la colección "notificaciones"
            db.collection("notificaciones")
                .add(notificacion)
                .addOnSuccessListener {
                    mostrarMensajeExito()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Error al guardar la notificación: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            Toast.makeText(this, "Debe completar todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarMensajeExito() {
        val toast = Toast.makeText(this, "Notificación enviada correctamente", Toast.LENGTH_LONG)
        val handler = Handler(Looper.getMainLooper())
        toast.show()
        handler.postDelayed({
            toast.cancel()
            finish()
        }, 3000)
    }
}
