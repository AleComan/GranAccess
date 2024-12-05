package com.example.granaccess.cuentas

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.granaccess.R
import com.google.firebase.firestore.FirebaseFirestore

class SeleccionarUsuario : AppCompatActivity() {

    private lateinit var linearLayouts: List<LinearLayout>
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private var usuarios: List<Usuario> = emptyList()
    private var currentPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.seleccionar_usuario)

        linearLayouts = listOf(
            findViewById(R.id.llUsuario1),
            findViewById(R.id.llUsuario2),
            findViewById(R.id.llUsuario3),
            findViewById(R.id.llUsuario4),
            findViewById(R.id.llUsuario5),
            findViewById(R.id.llUsuario6)
        )

        // Cargar usuarios desde Firestore
        cargarUsuarios()

        // Configurar flechas
        findViewById<ImageView>(R.id.ivLeftArrow).setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                mostrarUsuarios()
            }
        }

        findViewById<ImageView>(R.id.ivRightArrow).setOnClickListener {
            if ((currentPage + 1) * 6 < usuarios.size) {
                currentPage++
                mostrarUsuarios()
            }
        }

        linearLayouts.forEachIndexed() {
            index, linearLayout ->
            linearLayout.setOnClickListener {
                val usuario = usuarios[index]
                manejarSeleccionUsuario(usuario)
            }
        }
    }

    private fun cargarUsuarios() {
        firestore.collection("usuarios")
            .get()
            .addOnSuccessListener { querySnapshot ->

                if (querySnapshot.isEmpty) {
                    Log.e("Firestore", "La colección 'usuarios' está vacía.")
                    Toast.makeText(this, "No hay usuarios para mostrar.", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
                else {
                    Toast.makeText(this, "mu bien", Toast.LENGTH_SHORT).show()
                }

                usuarios = querySnapshot.documents.mapNotNull { document ->
                    val id = document.id
                    val username = document.getString("username") ?: "Sin nombre"
                    val icono = document.getString("icono") ?: ""
                    val role = document.getString("role") ?: "Sin rol"
                    val preference = document.getString("preferencia") ?: "none"
                    Usuario(id, username, icono, role, preference)
                }
                Toast.makeText(this, "${usuarios.size}", Toast.LENGTH_SHORT).show()
                mostrarUsuarios()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al cargar usuarios: ${e.message}", e)
            }
    }


    @SuppressLint("MissingInflatedId")
    private fun mostrarUsuarios() {
        val leftArrow = findViewById<ImageView>(R.id.ivLeftArrow)
        val rightArrow = findViewById<ImageView>(R.id.ivRightArrow)

        // Ocultar todas las vistas antes de mostrar las nuevas
        linearLayouts.forEach { it.visibility = View.GONE }

        // Calcular los usuarios a mostrar en la página actual
        val start = currentPage * 6
        val end = (start + 6).coerceAtMost(usuarios.size)
        val usuariosPagina = usuarios.subList(start, end)

        // Mostrar los usuarios correspondientes a la página actual
        for ((index, usuario) in usuariosPagina.withIndex()) {
            val layout = linearLayouts[index]
            layout.visibility = View.VISIBLE // Mostrar el LinearLayout

            // Asignar la imagen al ImageView
            val imageView = layout.findViewById<ImageView>(resources.getIdentifier("ivImagen${index + 1}", "id", packageName))
            Glide.with(this)
                .load(usuario.icono)
                .placeholder(R.drawable.placeholder_user) // Imagen por defecto mientras se carga
                .into(imageView)

            // Asignar el nombre de usuario al TextView
            val textView = layout.findViewById<TextView>(resources.getIdentifier("tvNombre${index + 1}", "id", packageName))
            textView.text = usuario.username
        }

        // Configurar visibilidad de las flechas
        leftArrow.visibility = if (currentPage > 0) View.VISIBLE else View.GONE
        rightArrow.visibility = if ((currentPage + 1) * 6 < usuarios.size) View.VISIBLE else View.GONE
    }

    private fun manejarSeleccionUsuario(usuario: Usuario) {
        when (usuario.role) {
            "profesor", "admin" -> {
                val intent = Intent(this, LoginTexto::class.java).apply {
                    putExtra("userID", usuario.id)
                    putExtra("username", usuario.username)
                }
                startActivity(intent)
            }
            "alumno" -> {
                val intent = if (usuario.preference == "text" || usuario.preference == "audio") {
                    Intent(this, LoginTexto::class.java)
                } else {
                    Intent(this, LoginImagen::class.java)
                }
                intent.putExtra("userID", usuario.id)
                intent.putExtra("username", usuario.username)
                startActivity(intent)
            }
            else -> {
                Toast.makeText(this, "Rol no soportado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
