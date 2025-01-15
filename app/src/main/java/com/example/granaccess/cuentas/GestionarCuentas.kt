package com.example.granaccess.cuentas

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.granaccess.R

class GestionarCuentas : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gestionar_cuentas)

        val btnRegistrarAlumno = findViewById<Button>(R.id.btnRegistrarAlumno)
        val btnRegistrarProfesor = findViewById<Button>(R.id.btnRegistrarProfesor)
        val btnBack = findViewById<Button>(R.id.backButton)

        // Navegar al registro de alumnos
        btnRegistrarAlumno.setOnClickListener {
            val intent = Intent(this, RegistroAlumno::class.java)
            startActivity(intent)
        }

        // Navegar al registro de profesores
        btnRegistrarProfesor.setOnClickListener {
            val intent = Intent(this, RegistroProfesor::class.java)
            startActivity(intent)
        }

        btnBack.setOnClickListener {
            val intent = Intent(this, VistaAdmin::class.java)
            startActivity(intent)
        }
    }
}
