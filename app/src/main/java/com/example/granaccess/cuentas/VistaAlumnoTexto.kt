package com.example.granaccess.cuentas

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.example.granaccess.R
import com.example.granaccess.tareas.TareasAlumno
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class VistaAlumnoTexto : ComponentActivity(){

    private var username: String? = null
    private var userID: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.vista_alumno_texto)

        val tareasButton = findViewById<Button>(R.id.bTareas)
        val backButton = findViewById<Button>(R.id.backButton)

        userID = intent.getStringExtra("userID") ?: ""
        username = intent.getStringExtra("username") ?: ""

        tareasButton.setOnClickListener {
            val intentTareas = Intent(this, TareasAlumno::class.java)
            intentTareas.putExtra("userID", userID)
            startActivity(intentTareas)
        }

        backButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intentBack = Intent(this, SeleccionarUsuario::class.java)
            startActivity(intentBack)
        }
    }
}