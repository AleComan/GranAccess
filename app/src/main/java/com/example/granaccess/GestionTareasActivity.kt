package com.example.granaccess

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class GestionTareasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_tareas)

        val btnCrearTarea: Button = findViewById(R.id.btnCrearTarea)
        val btnModificarTareas: Button = findViewById(R.id.btnModificarTareas)
        val btnVerTareas: Button = findViewById(R.id.btnVerTareas)

        btnCrearTarea.setOnClickListener {
            startActivity(Intent(this, CrearTareaActivity::class.java))
        }

        btnModificarTareas.setOnClickListener {
            // Asegúrate de que lleve a VerTareasActivity
            val intent = Intent(this, VerTareasActivity::class.java)
            startActivity(intent)
        }

        btnVerTareas.setOnClickListener {
            val intent = Intent(this, VerTareasActivity::class.java)
            startActivity(intent)
        }
    }
}
