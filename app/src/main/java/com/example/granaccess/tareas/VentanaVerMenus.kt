package com.example.granaccess.tareas

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.ArrayAdapter
import com.example.granaccess.R


class VentanaVerMenus : AppCompatActivity() {

    private lateinit var listViewMenus: ListView
    private val db = FirebaseFirestore.getInstance()
    private val menusList = mutableListOf<String>() // Lista para almacenar identificadores de los menús
    private val menuDocumentIds = mutableListOf<String>() // Lista para almacenar los IDs de los documentos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_menus)

        listViewMenus = findViewById(R.id.listViewMenus)

        // Cargar menús desde Firestore
        cargarMenusDesdeFirestore()

        // Configurar acción al pulsar un menú
        listViewMenus.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val menuId = menuDocumentIds[position]
                val intent = Intent(this, VentanaVerMenuIndividual::class.java).apply {
                    putExtra("MENU_ID", menuId)
                }
                startActivity(intent)
            }
    }

    private fun cargarMenusDesdeFirestore() {
        db.collection("menus")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val identificador = document.getString("identificador") ?: "Sin Nombre"
                    menusList.add(identificador)
                    menuDocumentIds.add(document.id) // Guardar el ID del documento
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, menusList)
                listViewMenus.adapter = adapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar los menús: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
