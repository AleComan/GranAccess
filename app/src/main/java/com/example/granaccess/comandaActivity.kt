package com.example.granaccess

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ComandaActivity : AppCompatActivity() {

    private lateinit var textEligeMaterialViews: List<TextView>
    private lateinit var editTextCantidadViews: List<EditText>
    private lateinit var textMensajeConfirmacion: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_comandas)

        // Referencias a los elementos de la interfaz
        val btnCancelar: Button = findViewById(R.id.btnCancelar)
        val btnConfirmarSeleccion: Button = findViewById(R.id.btnConfirmarSeleccion)
        textMensajeConfirmacion = findViewById(R.id.textMensajeConfirmacion)

        // Inicializar las listas de TextView y EditText
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

        // Lógica de acción para el botón "Cancelar"
        btnCancelar.setOnClickListener {
            Toast.makeText(this, "Operación cancelada", Toast.LENGTH_SHORT).show()
            finish() // Cierra la actividad actual y vuelve a la anterior
        }

        // Lógica de acción para el botón "Confirmar Selección"
        btnConfirmarSeleccion.setOnClickListener {
            var atLeastOneFilled = false
            var validQuantities = true

            for (i in textEligeMaterialViews.indices) {
                val material = textEligeMaterialViews[i].text.toString()
                val cantidad = editTextCantidadViews[i].text.toString()

                if (material != "Elige Material" && cantidad.isNotEmpty()) {
                    if (cantidad.toInt() > 0) {
                        atLeastOneFilled = true
                    } else {
                        validQuantities = false
                    }
                }
            }

            if (atLeastOneFilled && validQuantities) {
                // Mostrar el mensaje de confirmación y ocultarlo después de 3 segundos
                textMensajeConfirmacion.text = "Selección Confirmada"
                textMensajeConfirmacion.visibility = TextView.VISIBLE

                Handler(Looper.getMainLooper()).postDelayed({
                    textMensajeConfirmacion.visibility = TextView.GONE
                    val intent = Intent(this, MainActivity::class.java)
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

    companion object {
        const val REQUEST_CODE_SELECT_MATERIAL = 1
    }
}
