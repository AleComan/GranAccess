package com.example.granaccess.tareas

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.granaccess.R
import com.google.firebase.firestore.FirebaseFirestore

class VentanaVerNotificacionActivity : AppCompatActivity() {

    private lateinit var textTituloNotificacion: TextView
    private lateinit var textDescripcionNotificacion: TextView
    private lateinit var btnMarcarComoLeida: Button
    private lateinit var btnMarcarComoNoLeida: Button
    private lateinit var btnVolver: Button

    private val db = FirebaseFirestore.getInstance()
    private var notificacionId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_notificacion)

        // Enlazar las vistas con los id correspondientes en el layout XML
        textTituloNotificacion = findViewById(R.id.textTituloNotificacion)
        textDescripcionNotificacion = findViewById(R.id.textDescripcionNotificacion)
        btnMarcarComoLeida = findViewById(R.id.btnMarcarComoLeida)
        btnMarcarComoNoLeida = findViewById(R.id.btnMarcarComoNoLeida)
        btnVolver = findViewById(R.id.btnVolver)

        // Recuperar el ID de la notificación seleccionada
        notificacionId = intent.getStringExtra("notificacionId")
        if (notificacionId != null) {
            cargarNotificacionDesdeFirebase(notificacionId!!)
        }

        // Configuración de los botones
        btnMarcarComoLeida.setOnClickListener {
            actualizarEstadoNotificacion(true)
        }

        btnMarcarComoNoLeida.setOnClickListener {
            actualizarEstadoNotificacion(false)
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun cargarNotificacionDesdeFirebase(id: String) {
        db.collection("notificaciones")
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val asunto = document.getString("asunto") ?: "Sin Asunto"
                    val descripcion = document.getString("descripcion") ?: "Sin Descripción"
                    textTituloNotificacion.text = asunto
                    textDescripcionNotificacion.text = descripcion
                } else {
                    Toast.makeText(this, "No se encontró la notificación", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar la notificación: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun actualizarEstadoNotificacion(leida: Boolean) {
        notificacionId?.let { id ->
            db.collection("notificaciones")
                .document(id)
                .update("leida", leida)
                .addOnSuccessListener {
                    Toast.makeText(this, "Estado actualizado correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al actualizar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
