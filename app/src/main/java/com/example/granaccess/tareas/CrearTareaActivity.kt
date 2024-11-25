package com.example.granaccess.tareas

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.granaccess.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID

class CrearTareaActivity : AppCompatActivity() {

    private lateinit var editTituloTarea: EditText
    private lateinit var editDescripcionTarea: EditText
    private lateinit var textPaso: TextView
    private lateinit var editPasoContenido: EditText
    private lateinit var btnAnadirPaso: Button
    private lateinit var btnPasoSiguiente: Button
    private lateinit var btnPasoAnterior: Button
    private lateinit var btnCancelar: Button
    private lateinit var btnGuardarTarea: Button

    private var currentStep = 1
    private var stepsContent = mutableMapOf<Int, String>()
    private var stepAdded = false
    private var isEditing = false // Flag para modo de edición
    private var tareaId: String? = null // Identificador único de la tarea
    private var maxStep = 1
    private val tareasKey = "TAREAS_KEY"
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tarea)

        // Inicializar referencias a vistas
        editTituloTarea = findViewById(R.id.editTituloTarea)
        editDescripcionTarea = findViewById(R.id.editDescripcionTarea)
        textPaso = findViewById(R.id.textPaso)
        editPasoContenido = findViewById(R.id.editPasoContenido)
        btnAnadirPaso = findViewById(R.id.btnAnadirPaso)
        btnPasoSiguiente = findViewById(R.id.btnPasoSiguiente)
        btnPasoAnterior = findViewById(R.id.btnPasoAnterior)
        btnCancelar = findViewById(R.id.btnCancelar)
        btnGuardarTarea = findViewById(R.id.btnGuardarTarea)

        // Verificar si se pasó una tarea para editar
        val tareaJson = intent.getStringExtra("tareaSeleccionada")
        isEditing = intent.getBooleanExtra("isEditing", false)

        if (tareaJson != null) {
            val tarea = gson.fromJson(tareaJson, Tarea::class.java)
            loadTarea(tarea)
            tareaId = tarea.id // Asignar el ID de la tarea existente
            maxStep = stepsContent.size
        }

        updateStepDisplay()

        // Lógica de botones
        btnAnadirPaso.setOnClickListener {
            saveCurrentStepContent()
            stepAdded = true
            currentStep++
            maxStep = currentStep
            stepsContent[currentStep] = ""
            updateStepDisplay()
            editPasoContenido.text.clear()
        }

        btnPasoSiguiente.setOnClickListener {
            if (currentStep < maxStep) {
                saveCurrentStepContent()
                currentStep++
                updateStepDisplay()
                editPasoContenido.setText(stepsContent[currentStep] ?: "")
            }
        }

        btnPasoAnterior.setOnClickListener {
            if (currentStep > 1) {
                saveCurrentStepContent()
                currentStep--
                updateStepDisplay()
                editPasoContenido.setText(stepsContent[currentStep] ?: "")
            }
        }

        btnCancelar.setOnClickListener {
            finish()
        }

        btnGuardarTarea.setOnClickListener {
            saveCurrentStepContent()
            guardarTarea()
            showTaskCreatedMessage()
        }
    }

    private fun saveCurrentStepContent() {
        stepsContent[currentStep] = editPasoContenido.text.toString()
    }

    private fun updateStepDisplay() {
        textPaso.text = "Paso $currentStep"
        btnPasoAnterior.visibility = if (currentStep > 1) Button.VISIBLE else Button.GONE
        btnPasoSiguiente.isEnabled = currentStep < maxStep || stepAdded
        editPasoContenido.setText(stepsContent[currentStep] ?: "")
    }

    private fun loadTarea(tarea: Tarea) {
        editTituloTarea.setText(tarea.titulo)
        editDescripcionTarea.setText(tarea.descripcion)
        stepsContent = tarea.pasos.toMutableMap()
        currentStep = 1
        updateStepDisplay()
        editPasoContenido.setText(stepsContent[currentStep] ?: "")
    }

    private fun guardarTarea() {
        val titulo = editTituloTarea.text.toString()
        val descripcion = editDescripcionTarea.text.toString()

        if (titulo.isNotEmpty() && descripcion.isNotEmpty()) {
            val id = tareaId ?: UUID.randomUUID().toString() // Usar el ID existente o generar uno nuevo
            val tarea = Tarea(id, titulo, descripcion, stepsContent)
            val sharedPreferences = getSharedPreferences("TareasPref", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            val tareasJson = sharedPreferences.getString(tareasKey, null)
            val type = object : TypeToken<MutableList<Tarea>>() {}.type
            val listaTareas = if (tareasJson != null) {
                gson.fromJson<MutableList<Tarea>>(tareasJson, type)
            } else {
                mutableListOf()
            }

            // Remover la tarea existente con el mismo ID si se está editando
            val index = listaTareas.indexOfFirst { it.id == id }
            if (index != -1) {
                listaTareas[index] = tarea // Reemplazar la tarea existente
            } else {
                listaTareas.add(tarea) // Agregar como nueva tarea si no existe
            }

            editor.putString(tareasKey, gson.toJson(listaTareas))
            editor.apply()
        } else {
            Toast.makeText(this, "Debe completar todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showTaskCreatedMessage() {
        val toast = Toast.makeText(this, "Tarea guardada", Toast.LENGTH_LONG)
        val handler = Handler(Looper.getMainLooper())
        toast.show()
        handler.postDelayed({
            toast.cancel()
            finish()
        }, 5000)
    }
}
