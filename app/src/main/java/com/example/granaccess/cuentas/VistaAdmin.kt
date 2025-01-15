package com.example.granaccess.cuentas

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.granaccess.R
import com.example.granaccess.tareas.GestionTareasActivity
import com.example.granaccess.tareas.MenuTareas
import com.google.firebase.auth.FirebaseAuth

class VistaAdmin : AppCompatActivity() {

    private lateinit var userID: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vista_admin)

        userID = intent.getStringExtra("userID") ?: ""

        val btnGestionarCuentas = findViewById<Button>(R.id.btnGestionarCuentas)
        val btnGestionarTareas = findViewById<Button>(R.id.btnGestionarTareas)
        val btnOtros = findViewById<Button>(R.id.btnOtros)
        val btnSalir = findViewById<Button>(R.id.btnSalirAdmin)

        // Configurar el botón "Gestionar Cuentas"
        btnGestionarCuentas.setOnClickListener {
            val intent = Intent(this, GestionarCuentas::class.java)
            intent.putExtra("userID", userID)
            startActivity(intent)
        }

        // Configurar el botón "Gestionar Tareas"
        btnGestionarTareas.setOnClickListener {
            val intent = Intent(this, GestionTareasActivity::class.java)
            intent.putExtra("userID", userID)
            startActivity(intent)
        }

        btnOtros.setOnClickListener {
            val intent = Intent(this, MenuTareas::class.java)
            intent.putExtra("userID", userID)
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
