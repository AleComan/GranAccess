package com.example.granaccess.tareas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.granaccess.R
import com.google.firebase.firestore.FirebaseFirestore

class OpcionesModificarTarea : AppCompatActivity() {

    private lateinit var tareaID: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.opciones_modificar_tarea)

        tareaID = intent.getStringExtra("tareaID") ?: ""

        val btnModificarInformacion = findViewById<Button>(R.id.btnModificarInformacion)
        val btnCambiarOrdenPasos = findViewById<Button>(R.id.btnCambiarOrdenPasos)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        btnModificarInformacion.setOnClickListener {
            val intent = Intent(this, ModificarTareaActivity::class.java)
            intent.putExtra("tareaID", tareaID)
            startActivity(intent)
        }

        btnCambiarOrdenPasos.setOnClickListener {
            val intent = Intent(this, CambiarPasosActivity::class.java)
            intent.putExtra("tareaID", tareaID)
            startActivity(intent)
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }
}
