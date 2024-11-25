package com.example.granaccess.cuentas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.granaccess.R
import com.google.firebase.firestore.FirebaseFirestore

class SeleccionarUsuario : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: UsuarioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.seleccionar_usuario)

        db = FirebaseFirestore.getInstance()
        val recyclerView = findViewById<RecyclerView>(R.id.rvUsuarios)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Obtener usuarios desde Firestore
        db.collection("usuarios").get()
            .addOnSuccessListener { result ->
                val usuarios = result.documents.mapNotNull { document ->
                    Usuario(
                        id = document.id,
                        username = document.getString("username") ?: "",
                        icono = document.getString("icono") ?: "",
                        role = document.getString("role") ?: "",
                        imgPref = document.getBoolean("imgPref") ?: false
                    )
                }
                adapter = UsuarioAdapter(usuarios) { usuario ->
                    manejarSeleccionUsuario(usuario)
                }
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, ": ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun manejarSeleccionUsuario(usuario: Usuario) {
        when (usuario.role) {
            "admin", "profesor" -> {
                val intent = Intent(this, LoginTexto::class.java)
                intent.putExtra("userId", usuario.id)
                startActivity(intent)
            }
            "alumno" -> {
                if (usuario.imgPref) {
                    val intent = Intent(this, LoginImagen::class.java)
                    intent.putExtra("userId", usuario.id)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, LoginTexto::class.java)
                    intent.putExtra("userId", usuario.id)
                    startActivity(intent)
                }
            }
            else -> {
                Toast.makeText(this, "Rol no soportado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
