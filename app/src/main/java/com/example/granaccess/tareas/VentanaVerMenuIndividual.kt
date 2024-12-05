package com.example.granaccess.tareas

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.granaccess.R
import com.google.firebase.firestore.FirebaseFirestore

class VentanaVerMenuIndividual : AppCompatActivity() {

    private lateinit var textIdentificador: TextView
    private lateinit var textTipoMenu: TextView
    private lateinit var textPrimerPlato: TextView
    private lateinit var textSegundoPlato: TextView
    private lateinit var textPostre: TextView
    private lateinit var textAsignaciones: TextView

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_menu_individual)

        // Inicializar vistas
        textIdentificador = findViewById(R.id.textIdentificador)
        textTipoMenu = findViewById(R.id.textTipoMenu)
        textPrimerPlato = findViewById(R.id.textPrimerPlato)
        textSegundoPlato = findViewById(R.id.textSegundoPlato)
        textPostre = findViewById(R.id.textPostre)
        textAsignaciones = findViewById(R.id.textAsignaciones)

        // Recuperar el identificador del menú seleccionado
        val menuId = intent.getStringExtra("MENU_ID") ?: return

        // Cargar datos del menú desde Firestore
        db.collection("menus").document(menuId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Mostrar identificador
                    val identificador = document.getString("identificador") ?: "Sin Identificador"
                    textIdentificador.text = "Identificador: $identificador"

                    // Mostrar información del menú
                    val menus = document.get("menus") as? List<Map<String, String>>
                    if (!menus.isNullOrEmpty()) {
                        val menuData = menus[0]
                        val tipo = menuData["tipo"] ?: "Desconocido"
                        val primerPlato = menuData["primerplato"] ?: "No especificado"
                        val segundoPlato = menuData["segundoplato"] ?: "No especificado"
                        val postre = menuData["postre"] ?: "No especificado"

                        textTipoMenu.text = "Tipo de Menú: $tipo"
                        textPrimerPlato.text = "Primer Plato: $primerPlato"
                        textSegundoPlato.text = "Segundo Plato: $segundoPlato"
                        textPostre.text = "Postre: $postre"
                    } else {
                        textTipoMenu.text = "Tipo de Menú: No especificado"
                        textPrimerPlato.text = "Primer Plato: No especificado"
                        textSegundoPlato.text = "Segundo Plato: No especificado"
                        textPostre.text = "Postre: No especificado"
                    }

                    // Mostrar asignaciones
                    val asignaciones = document.get("asignaciones") as? List<Map<String, Any>>
                    if (!asignaciones.isNullOrEmpty()) {
                        val asignacionesText = asignaciones.joinToString(separator = "\n") { asignacion ->
                            val alumno = asignacion["alumno"] ?: "Desconocido"
                            val cantidad = asignacion["cantidad"] ?: 0
                            "$alumno: $cantidad"
                        }
                        textAsignaciones.text = "Asignaciones:\n$asignacionesText"
                    } else {
                        textAsignaciones.text = "Asignaciones: Ninguna"
                    }
                } else {
                    Toast.makeText(this, "Menú no encontrado", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar el menú", Toast.LENGTH_SHORT).show()
                finish()
            }
    }
}
