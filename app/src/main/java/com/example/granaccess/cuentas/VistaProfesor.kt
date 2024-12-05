package com.example.granaccess.cuentas

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.example.granaccess.R
import com.google.firebase.auth.FirebaseAuth

class VistaProfesor : ComponentActivity(){

    private lateinit var userID: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.vista_alumno_texto)

        userID = intent.getStringExtra("userID") ?: ""

        val backButton = findViewById<ImageView>(R.id.ivBackArrow)

        backButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intentBack = Intent(this, SeleccionarUsuario::class.java)
            startActivity(intentBack)
        }
    }
}