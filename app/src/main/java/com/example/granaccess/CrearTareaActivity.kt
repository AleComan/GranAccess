package com.example.granaccess

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
    private var isEditing = false // Variable para controlar si estás en modo de edición
    private var maxStep = 1 // Controla el número máximo de pasos en el modo de edición
    private val tareasKey = "TAREAS_KEY"
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_tarea)

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
        if (tareaJson != null) {
            val tarea = gson.fromJson(tareaJson, Tarea::class.java)
            loadTarea(tarea)
            isEditing = true // Activar modo de edición
            maxStep = stepsContent.size // Establecer el número máximo de pasos
        }

        updateStepDisplay()

        // Lógica de botones
        btnAnadirPaso.setOnClickListener {
            saveCurrentStepContent()
            stepAdded = true
            currentStep++
            maxStep = currentStep // Actualizar el número máximo de pasos cuando se agrega uno nuevo
            stepsContent[currentStep] = ""
            updateStepDisplay()
            editPasoContenido.text.clear()
        }

        btnPasoSiguiente.setOnClickListener {
            if ((stepAdded || isEditing) && currentStep < maxStep) {
                saveCurrentStepContent()
                currentStep++
                updateStepDisplay()
                editPasoContenido.setText(stepsContent[currentStep] ?: "")
            } else {
                Toast.makeText(this, "Debe agregar un paso antes de continuar", Toast.LENGTH_SHORT).show()
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
            val tarea = Tarea(titulo, descripcion, stepsContent)
            val sharedPreferences = getSharedPreferences("TareasPref", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            val tareasJson = sharedPreferences.getString(tareasKey, null)
            val type = object : TypeToken<MutableList<Tarea>>() {}.type
            val listaTareas = if (tareasJson != null) {
                gson.fromJson<MutableList<Tarea>>(tareasJson, type)
            } else {
                mutableListOf()
            }

            // Reemplazar la tarea existente si se está editando
            if (isEditing) {
                val index = listaTareas.indexOfFirst { it.titulo == tarea.titulo }
                if (index != -1) {
                    listaTareas[index] = tarea
                }
            } else {
                listaTareas.add(tarea)
            }

            editor.putString(tareasKey, gson.toJson(listaTareas))
            editor.apply()
        } else {
            Toast.makeText(this, "Debe completar todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showTaskCreatedMessage() {
        val toast = Toast.makeText(this, "Tarea creada", Toast.LENGTH_LONG)
        val handler = Handler(Looper.getMainLooper())
        toast.show()
        handler.postDelayed({
            toast.cancel()
            finish()
        }, 5000)
    }
}
