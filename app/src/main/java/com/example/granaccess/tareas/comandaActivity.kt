package com.example.granaccess.tareas

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.granaccess.R
import com.google.firebase.firestore.FirebaseFirestore

class ComandaActivity : AppCompatActivity() {

    private lateinit var editNombreLista: EditText
    private lateinit var textEligeMaterialViews: List<TextView>
    private lateinit var editTextCantidadViews: List<EditText>
    private lateinit var spinnerColorViews: List<Spinner>
    private lateinit var textMensajeConfirmacion: TextView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_comandas)

        // Referencias a los elementos de la interfaz
        editNombreLista = findViewById(R.id.editNombreLista)
        val btnCancelar: Button = findViewById(R.id.btnCancelar)
        val btnConfirmarSeleccion: Button = findViewById(R.id.btnConfirmarSeleccion)
        textMensajeConfirmacion = findViewById(R.id.textMensajeConfirmacion)

        // Inicializar las listas de TextView, EditText y Spinner
        textEligeMaterialViews = listOf(
            findViewById(R.id.textEligeMaterial1),
            findViewById(R.id.textEligeMaterial2),
            findViewById(R.id.textEligeMaterial3),
            findViewById(R.id.textEligeMaterial4),
            findViewById(R.id.textEligeMaterial5)
        )

        editTextCantidadViews = listOf(
            findViewById(R.id.editTextCantidad1),
            findViewById(R.id.editTextCantidad2),
            findViewById(R.id.editTextCantidad3),
            findViewById(R.id.editTextCantidad4),
            findViewById(R.id.editTextCantidad5)
        )

        spinnerColorViews = listOf(
            findViewById(R.id.spinnerColor1),
            findViewById(R.id.spinnerColor2),
            findViewById(R.id.spinnerColor3),
            findViewById(R.id.spinnerColor4),
            findViewById(R.id.spinnerColor5)
        )

        // Opciones de colores (con "Sin color" como opción predeterminada)
        val colores = listOf("Sin color", "Rojo", "Azul", "Verde", "Amarillo", "Negro", "Blanco", "Gris")

        // Configurar cada Spinner con las opciones de colores
        spinnerColorViews.forEach { spinner ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, colores)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        // Lógica de acción para el botón "Cancelar"
        btnCancelar.setOnClickListener {
            Toast.makeText(this, "Operación cancelada", Toast.LENGTH_SHORT).show()
            finish() // Cierra la actividad actual y vuelve a la anterior
        }

        // Lógica de acción para el botón "Confirmar Selección"
        btnConfirmarSeleccion.setOnClickListener {
            val nombreLista = editNombreLista.text.toString().trim()
            if (nombreLista.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese un nombre para la lista", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var atLeastOneFilled = false
            var validQuantities = true

            val listaMateriales = mutableListOf<Map<String, Any>>() // Lista para almacenar los materiales seleccionados

            for (i in textEligeMaterialViews.indices) {
                val material = textEligeMaterialViews[i].text.toString()
                val cantidad = editTextCantidadViews[i].text.toString()
                val color = spinnerColorViews[i].selectedItem?.toString() ?: "Sin color"

                if (material != "Elige Material" && cantidad.isNotEmpty()) {
                    if (cantidad.toInt() > 0) {
                        atLeastOneFilled = true
                        listaMateriales.add(
                            mapOf(
                                "material" to material,
                                "cantidad" to cantidad.toInt(),
                                "color" to color
                            )
                        )
                    } else {
                        validQuantities = false
                    }
                }
            }

            if (atLeastOneFilled && validQuantities) {
                guardarListaEnFirestore(nombreLista, listaMateriales)

                // Mostrar el mensaje de confirmación y ocultarlo después de 3 segundos
                textMensajeConfirmacion.text = "Lista guardada correctamente"
                textMensajeConfirmacion.visibility = TextView.VISIBLE

                Handler(Looper.getMainLooper()).postDelayed({
                    textMensajeConfirmacion.visibility = TextView.GONE
                    val intent = Intent(this, MenuTareas::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }, 3000)
            } else {
                if (!validQuantities) {
                    Toast.makeText(this, "Las cantidades deben ser mayores a 0", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Debe completar al menos un campo", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Lógica para abrir la actividad de Seleccionar Material
        for (i in textEligeMaterialViews.indices) {
            val textView = textEligeMaterialViews[i]
            textView.setOnClickListener {
                val intent = Intent(this, SeleccionarMaterialActivity::class.java)
                intent.putExtra("MATERIAL_INDEX", i)
                startActivityForResult(intent, REQUEST_CODE_SELECT_MATERIAL)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_MATERIAL && resultCode == RESULT_OK) {
            val materialSeleccionado = data?.getStringExtra("MATERIAL_SELECCIONADO")
            val materialIndex = data?.getIntExtra("MATERIAL_INDEX", -1) ?: -1

            if (materialIndex in textEligeMaterialViews.indices) {
                materialSeleccionado?.let {
                    textEligeMaterialViews[materialIndex].text = it
                }
            }
        }
    }

    private fun guardarListaEnFirestore(nombreLista: String, listaMateriales: List<Map<String, Any>>) {
        val datos: Map<String, Any> = mapOf(
            "identificador" to nombreLista,
            "materialesAsignados" to listaMateriales
        )

        db.collection("materiales")
            .add(datos)
            .addOnSuccessListener {
                Toast.makeText(this, "Lista guardada correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar la lista: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        const val REQUEST_CODE_SELECT_MATERIAL = 1
    }
}
