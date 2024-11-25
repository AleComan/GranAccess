package com.example.granaccess

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.example.granaccess.cuentas.SeleccionarUsuario

class MainActivity : ComponentActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val loginButton = findViewById<Button>(R.id.loginButton)
        // Configura el evento de clic en el botón

        loginButton.setOnClickListener {
            val intent = Intent(this, SeleccionarUsuario::class.java)
            startActivity(intent)
        }
    }
}
