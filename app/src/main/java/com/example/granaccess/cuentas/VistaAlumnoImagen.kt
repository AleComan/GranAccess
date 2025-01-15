package com.example.granaccess.cuentas

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.example.granaccess.R
import com.example.granaccess.tareas.TareasAlumno
import com.google.firebase.auth.FirebaseAuth

class VistaAlumnoImagen : ComponentActivity(){

    private var username: String? = null
    private var userID: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.vista_alumno_imagen)

        intent.putExtra("username", username)
        intent.putExtra("userID", userID)

        val backButton = findViewById<ImageView>(R.id.ivBackArrow)
        val tareasButton = findViewById<ImageButton>(R.id.ibTareas)

        tareasButton.setOnClickListener {
            val intent = Intent(this, TareasAlumno::class.java)
            intent.putExtra("username", username)
            intent.putExtra("userID", userID)
            startActivity(intent)
        }

        backButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intentBack = Intent(this, SeleccionarUsuario::class.java)
            startActivity(intentBack)
        }
    }
}