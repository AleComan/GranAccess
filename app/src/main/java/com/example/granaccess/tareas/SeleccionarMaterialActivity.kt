package com.example.granaccess.tareas

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.granaccess.R

class SeleccionarMaterialActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccionar_material)

        // Habilita el botón de navegación hacia atrás
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Lista de ejemplo de materiales
        val materiales = listOf("Papel Blanco", "Lápices", "Bolígrafos", "Gomas de borrar", "Tijeras",
            "Pegamento", "Lápices de colores", "Calculadoras", "Cuadernos", "Carpeta")

        // Referencia al ListView
        val listViewMateriales: ListView = findViewById(R.id.listViewMateriales)

        // Adaptador para mostrar la lista
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, materiales)
        listViewMateriales.adapter = adapter

        // Acción al seleccionar un material
        listViewMateriales.setOnItemClickListener { _, _, position, _ ->
            val materialSeleccionado = materiales[position]
            val materialIndex = intent.getIntExtra("MATERIAL_INDEX", -1) // Recibir el índice pasado

            val resultIntent = Intent().apply {
                putExtra("MATERIAL_SELECCIONADO", materialSeleccionado)
                putExtra("MATERIAL_INDEX", materialIndex) // Pasar de vuelta el índice
            }
            setResult(RESULT_OK, resultIntent)
            finish() // Vuelve a la actividad anterior y envía el resultado
        }
    }

    // Maneja la acción de la flecha de navegación hacia atrás
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
