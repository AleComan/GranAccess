package com.example.granaccess.tareas
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.granaccess.R
import com.example.granaccess.cuentas.VistaAdmin

class GestionTareasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_tareas)

        val btnCrearTarea = findViewById<Button>(R.id.btnCrearTarea)
        val btnModificarTareas = findViewById<Button>(R.id.btnModificarTareas)
        val btnVerTareas = findViewById<Button>(R.id.btnVerTareas)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        btnCrearTarea.setOnClickListener {
            val intent = Intent(this, CrearTareaActivity::class.java)
            startActivity(intent)
        }

        btnModificarTareas.setOnClickListener {
            val intent = Intent(this, ListaModificarActivity::class.java)
            startActivity(intent)
        }

        btnVolver.setOnClickListener {
            val intent = Intent(this, VistaAdmin::class.java)
            startActivity(intent)
        }
    }
}
