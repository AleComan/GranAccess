package com.example.granaccess.tareas

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.granaccess.R
import com.google.firebase.firestore.FirebaseFirestore

class VentanaAsignarMenus : AppCompatActivity() {

    private lateinit var editNombreAlumno: TextView
    private lateinit var textNombreAlumno: TextView
    private lateinit var textNombreMenu: TextView
    private lateinit var textCantidad: TextView
    private lateinit var btnMenos: Button
    private lateinit var btnMas: Button
    private lateinit var btnSiguienteMenu: Button
    private lateinit var btnAnteriorMenu: Button
    private lateinit var btnConfirmarSeleccion: Button
    private lateinit var btnAsignarAlumno: Button

    private val db = FirebaseFirestore.getInstance()
    private var menus = listOf<Map<String, Any>>() // Lista de menús recuperados de la base de datos
    private var menuIndex = 0 // Índice del menú actual
    private val cantidadesSeleccionadas = mutableMapOf<String, Int>() // Cantidades seleccionadas por menú
    private var alumnoActual: String? = null // Nombre del alumno actual

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asignar_menus)

        // Inicializar vistas
        editNombreAlumno = findViewById(R.id.editNombreAlumno)
        textNombreAlumno = findViewById(R.id.textNombreAlumno)
        textNombreMenu = findViewById(R.id.textNombreMenu)
        textCantidad = findViewById(R.id.textCantidad)
        btnMenos = findViewById(R.id.btnMenos)
        btnMas = findViewById(R.id.btnMas)
        btnSiguienteMenu = findViewById(R.id.btnSiguienteMenu)
        btnAnteriorMenu = findViewById(R.id.btnAnteriorMenu)
        btnConfirmarSeleccion = findViewById(R.id.btnConfirmarSeleccion)
        btnAsignarAlumno = findViewById(R.id.btnAsignarAlumno)

        // Configurar botón Asignar Alumno
        btnAsignarAlumno.setOnClickListener {
            val nombre = editNombreAlumno.text.toString().trim()
            if (nombre.isNotEmpty()) {
                alumnoActual = nombre
                textNombreAlumno.text = "Asignando a: $nombre"
                Toast.makeText(this, "Alumno asignado correctamente", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Ingrese un nombre válido", Toast.LENGTH_SHORT).show()
            }
        }

        // Recuperar menús de la base de datos
        db.collection("menus")
            .get()
            .addOnSuccessListener { result ->
                menus = result.documents.mapNotNull { it.data }
                if (menus.isNotEmpty()) {
                    mostrarMenuActual()
                } else {
                    Toast.makeText(this, "No hay menús disponibles", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar los menús", Toast.LENGTH_SHORT).show()
            }

        // Configurar botones
        btnMenos.setOnClickListener { disminuirCantidad() }
        btnMas.setOnClickListener { aumentarCantidad() }
        btnSiguienteMenu.setOnClickListener { siguienteMenu() }
        btnAnteriorMenu.setOnClickListener { anteriorMenu() }
        btnConfirmarSeleccion.setOnClickListener { confirmarSeleccion() }
    }

    private fun mostrarMenuActual() {
        val menuActual = menus[menuIndex]
        val nombreMenu = menuActual["identificador"] as? String ?: "Sin Nombre"
        textNombreMenu.text = nombreMenu

        // Mostrar la cantidad seleccionada para este menú (por defecto 0)
        val cantidad = cantidadesSeleccionadas[nombreMenu] ?: 0
        textCantidad.text = cantidad.toString()

        // Configurar visibilidad de los botones Anterior y Siguiente
        btnAnteriorMenu.isEnabled = menuIndex > 0
        btnSiguienteMenu.isEnabled = menuIndex < menus.size - 1
    }

    private fun disminuirCantidad() {
        val menuActual = menus[menuIndex]
        val nombreMenu = menuActual["identificador"] as? String ?: "Sin Nombre"
        val cantidadActual = cantidadesSeleccionadas[nombreMenu] ?: 0
        if (cantidadActual > 0) {
            cantidadesSeleccionadas[nombreMenu] = cantidadActual - 1
            textCantidad.text = (cantidadActual - 1).toString()
        }
    }

    private fun aumentarCantidad() {
        val menuActual = menus[menuIndex]
        val nombreMenu = menuActual["identificador"] as? String ?: "Sin Nombre"
        val cantidadActual = cantidadesSeleccionadas[nombreMenu] ?: 0
        cantidadesSeleccionadas[nombreMenu] = cantidadActual + 1
        textCantidad.text = (cantidadActual + 1).toString()
    }

    private fun siguienteMenu() {
        if (menuIndex < menus.size - 1) {
            menuIndex++
            mostrarMenuActual()
        }
    }

    private fun anteriorMenu() {
        if (menuIndex > 0) {
            menuIndex--
            mostrarMenuActual()
        }
    }

    private fun confirmarSeleccion() {
        if (alumnoActual.isNullOrEmpty()) {
            Toast.makeText(this, "Debe asignar un alumno antes de confirmar", Toast.LENGTH_SHORT).show()
            return
        }

        if (cantidadesSeleccionadas.isNotEmpty()) {
            val batch = db.batch()

            cantidadesSeleccionadas.forEach { (menu, cantidad) ->
                val menuDoc = menus.find { it["identificador"] == menu }
                menuDoc?.let {
                    val docRef = db.collection("menus").document(menu)
                    val asignaciones =
                        (menuDoc["asignaciones"] as? MutableList<Map<String, Any>>)?.toMutableList()
                            ?: mutableListOf()

                    asignaciones.add(
                        mapOf(
                            "alumno" to alumnoActual!!,
                            "cantidad" to cantidad
                        )
                    )

                    batch.update(docRef, "asignaciones", asignaciones)
                }
            }

            batch.commit()
                .addOnSuccessListener {
                    Toast.makeText(this, "Asignación confirmada correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al guardar la asignación", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No has seleccionado ninguna cantidad", Toast.LENGTH_SHORT).show()
        }
    }
}
