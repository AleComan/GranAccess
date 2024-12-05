package com.example.granaccess.tareas

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.granaccess.R
import com.google.firebase.firestore.FirebaseFirestore

class VentanaVerListasMaterial : AppCompatActivity() {

    private lateinit var listViewListasMaterial: ListView
    private val db = FirebaseFirestore.getInstance()
    private val identificadoresList = mutableListOf<String>() // Lista de identificadores

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_listas_material)

        listViewListasMaterial = findViewById(R.id.listViewListasMaterial)

        // Cargar identificadores de la base de datos
        cargarIdentificadoresDesdeFirestore()

        // Acción al pulsar una lista
        listViewListasMaterial.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val identificadorSeleccionado = identificadoresList[position]
                val intent = Intent(this, VentanaVerListaMaterialIndividual::class.java)
                intent.putExtra("identificadorSeleccionado", identificadorSeleccionado)
                startActivity(intent)
            }
    }

    private fun cargarIdentificadoresDesdeFirestore() {
        db.collection("materiales")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val identificador = document.getString("identificador")
                    if (identificador != null) {
                        identificadoresList.add(identificador)
                    }
                }
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    identificadoresList
                )
                listViewListasMaterial.adapter = adapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar listas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
