package com.example.granaccess.tareas

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.granaccess.R
import com.google.firebase.firestore.FirebaseFirestore

class MenuActivity : AppCompatActivity() {

    private lateinit var editNombreMenu: EditText
    private lateinit var spinnerTipoMenu: Spinner
    private lateinit var editPrimerPlato: EditText
    private lateinit var editSegundoPlato: EditText
    private lateinit var editPostre: EditText
    private lateinit var btnAgregarMenu: Button
    private val db = FirebaseFirestore.getInstance()
    private val menusList = mutableListOf<Map<String, Any>>() // Lista de menús

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // Inicializar las vistas
        editNombreMenu = findViewById(R.id.editNombreMenu)
        spinnerTipoMenu = findViewById(R.id.spinnerTipoMenu)
        editPrimerPlato = findViewById(R.id.editPrimerPlato)
        editSegundoPlato = findViewById(R.id.editSegundoPlato)
        editPostre = findViewById(R.id.editPostre)
        btnAgregarMenu = findViewById(R.id.btnAgregarMenu)

        // Configurar el spinner de tipos de menú
        val tiposMenu = listOf("Carnívoro", "Sin Gluten", "Vegano")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tiposMenu)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoMenu.adapter = adapter

        // Acción del botón "Agregar Menú"
        btnAgregarMenu.setOnClickListener {
            agregarMenu()
        }
    }

    private fun agregarMenu() {
        val nombreMenu = editNombreMenu.text.toString()
        val tipoMenu = spinnerTipoMenu.selectedItem.toString()
        val primerPlato = editPrimerPlato.text.toString()
        val segundoPlato = editSegundoPlato.text.toString()
        val postre = editPostre.text.toString()

        if (nombreMenu.isNotEmpty() &&
            primerPlato.isNotEmpty() &&
            segundoPlato.isNotEmpty() &&
            postre.isNotEmpty()
        ) {
            val nuevoMenu = mapOf(
                "tipo" to tipoMenu.lowercase(),
                "primerplato" to primerPlato,
                "segundoplato" to segundoPlato,
                "postre" to postre
            )

            menusList.add(nuevoMenu)

            // Guardar directamente en la base de datos
            val menuCompleto = mapOf(
                "identificador" to nombreMenu,
                "menus" to menusList
            )

            db.collection("menus")
                .document(nombreMenu) // Usar el nombre del menú como ID del documento
                .set(menuCompleto)
                .addOnSuccessListener {
                    Toast.makeText(this, "Menú agregado correctamente", Toast.LENGTH_SHORT).show()
                    limpiarCampos()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al guardar el menú: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Complete todos los campos correctamente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun limpiarCampos() {
        editPrimerPlato.text.clear()
        editSegundoPlato.text.clear()
        editPostre.text.clear()
    }
}
