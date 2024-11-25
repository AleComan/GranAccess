package com.example.granaccess.tareas

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.granaccess.R
import com.google.gson.Gson

class VentanaVerNotificacionActivity : AppCompatActivity() {

    private lateinit var textTituloNotificacion: TextView
    private lateinit var textDescripcionNotificacion: TextView
    private lateinit var btnMarcarComoLeida: Button
    private lateinit var btnMarcarComoNoLeida: Button
    private lateinit var btnVolver: Button
    private val gson = Gson()
    private val notificacionesKey = "NOTIFICACIONES_KEY"
    private lateinit var notificacion: Notificacion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_notificacion) // Asegúrate de que el nombre sea correcto

        // Enlazar las vistas con los id correspondientes en el layout XML
        textTituloNotificacion = findViewById(R.id.textTituloNotificacion)
        textDescripcionNotificacion = findViewById(R.id.textDescripcionNotificacion)
        btnMarcarComoLeida = findViewById(R.id.btnMarcarComoLeida)
        btnMarcarComoNoLeida = findViewById(R.id.btnMarcarComoNoLeida)
        btnVolver = findViewById(R.id.btnCancelar)

        // Recuperar los datos de la notificación seleccionada
        val notificacionJson = intent.getStringExtra("notificacionSeleccionada")
        if (notificacionJson != null) {
            notificacion = gson.fromJson(notificacionJson, Notificacion::class.java)
            mostrarNotificacion(notificacion)
        }

        // Configuración de los botones
        btnMarcarComoLeida.setOnClickListener {
            actualizarEstadoNotificacion(true)
            finish()
        }

        btnMarcarComoNoLeida.setOnClickListener {
            actualizarEstadoNotificacion(false)
            finish()
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun mostrarNotificacion(notificacion: Notificacion) {
        textTituloNotificacion.text = notificacion.asunto
        textDescripcionNotificacion.text = notificacion.descripcion
    }

    private fun actualizarEstadoNotificacion(leida: Boolean) {
        val sharedPreferences = getSharedPreferences("NotificacionesPref", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()

        // Recuperar lista de notificaciones
        val notificacionesJson = sharedPreferences.getString(notificacionesKey, null)
        val type = object : com.google.gson.reflect.TypeToken<MutableList<Notificacion>>() {}.type
        val listaNotificaciones = if (notificacionesJson != null) {
            gson.fromJson<MutableList<Notificacion>>(notificacionesJson, type)
        } else {
            mutableListOf()
        }

        // Actualizar el estado de la notificación
        val index = listaNotificaciones.indexOfFirst { it.id == notificacion.id }
        if (index != -1) {
            listaNotificaciones[index].leida = leida
            editor.putString(notificacionesKey, gson.toJson(listaNotificaciones))
            editor.apply()
        }
    }
}
