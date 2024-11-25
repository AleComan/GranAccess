package com.example.granaccess.cuentas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.granaccess.R
import com.google.firebase.auth.FirebaseAuth

class VistaAdmin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vista_admin)

        val btnGestionarCuentas = findViewById<Button>(R.id.btnGestionarCuentas)
        val btnSalir = findViewById<Button>(R.id.btnSalirAdmin)

        // Configurar el botón "Gestionar Cuentas"
        btnGestionarCuentas.setOnClickListener {
            val intent = Intent(this, GestionarCuentas::class.java)
            startActivity(intent)
        }

        // Configurar el botón "Salir"
        btnSalir.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, SeleccionarUsuario::class.java))
            finish()
        }
    }
}
