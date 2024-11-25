package com.example.granaccess.tareas

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.granaccess.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID

class CrearNotificacionActivity : AppCompatActivity() {

    private lateinit var editAsuntoNotificacion: EditText
    private lateinit var editDescripcionNotificacion: EditText
    private lateinit var btnCancelar: Button
    private lateinit var btnEnviarNotificacion: Button

    private val notificacionesKey = "NOTIFICACIONES_KEY"
    private val gson = Gson()

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
            guardarNotificacion()
            showNotificationSentMessage()
        }
    }

    private fun guardarNotificacion() {
        val asunto = editAsuntoNotificacion.text.toString()
        val descripcion = editDescripcionNotificacion.text.toString()

        if (asunto.isNotEmpty() && descripcion.isNotEmpty()) {
            val id = UUID.randomUUID().toString()
            val notificacion = Notificacion(id, asunto, descripcion)
            val sharedPreferences = getSharedPreferences("NotificacionesPref", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            val notificacionesJson = sharedPreferences.getString(notificacionesKey, null)
            val type = object : TypeToken<MutableList<Notificacion>>() {}.type
            val listaNotificaciones = if (notificacionesJson != null) {
                gson.fromJson<MutableList<Notificacion>>(notificacionesJson, type)
            } else {
                mutableListOf()
            }

            // Añadir la nueva notificación a la lista
            listaNotificaciones.add(notificacion)

            editor.putString(notificacionesKey, gson.toJson(listaNotificaciones))
            editor.apply()
        } else {
            Toast.makeText(this, "Debe completar todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showNotificationSentMessage() {
        val toast = Toast.makeText(this, "Notificación enviada", Toast.LENGTH_LONG)
        val handler = Handler(Looper.getMainLooper())
        toast.show()
        handler.postDelayed({
            toast.cancel()
            finish()
        }, 5000)
    }
}
