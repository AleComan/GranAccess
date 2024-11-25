package com.example.granaccess.cuentas

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.example.granaccess.R

class VistaProfesor : ComponentActivity(){
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.vista_alumno)

        val backButton = findViewById<Button>(R.id.backToMain)

        backButton.setOnClickListener {
            val intentBack = Intent(this, SeleccionarUsuario::class.java)
            startActivity(intentBack)
        }
    }
}