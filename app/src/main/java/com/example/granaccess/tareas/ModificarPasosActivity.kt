package com.example.granaccess.tareas

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.granaccess.R
import java.util.Collections

class ModificarPasosActivity : AppCompatActivity() {

    private lateinit var tvNumeroPaso: TextView
    private lateinit var etDescripcionPaso: EditText
    private lateinit var ivImagenPaso: ImageView
    private lateinit var btnAnteriorPaso: Button
    private lateinit var btnSiguientePaso: Button
    private lateinit var btnGuardarTarea: Button
    private lateinit var btnVolver: Button

    private var pasosOriginales: ArrayList<PasoTarea> = arrayListOf()
    private var pasosModificados: ArrayList<PasoTarea?> = arrayListOf()
    private var currentPasoIndex: Int = 0

    private var tareaID: String? = null
    private var titulo: String? = null
    private var descripcion: String? = null
    private var imagen: String? = null
    private var asignados: ArrayList<asignadosParcelable> = arrayListOf()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificar_pasos)

        // Inicializar vistas
        tvNumeroPaso = findViewById(R.id.textPaso)
        etDescripcionPaso = findViewById(R.id.editPasoContenido)
        ivImagenPaso = findViewById(R.id.ivImagenPaso)
        btnAnteriorPaso = findViewById(R.id.btnPasoAnterior)
        btnSiguientePaso = findViewById(R.id.btnPasoSiguiente)
        btnGuardarTarea = findViewById(R.id.btnGuardarTarea)
        btnVolver = findViewById(R.id.btnVolver)

        // Recuperar datos del intent
        tareaID = intent.getStringExtra("tareaID")
        titulo = intent.getStringExtra("titulo")
        descripcion = intent.getStringExtra("descripcion")
        imagen = intent.getStringExtra("imagen")
        pasosOriginales = intent.getParcelableArrayListExtra("pasos", PasoTarea::class.java) ?: arrayListOf()
        asignados = intent.getParcelableArrayListExtra("asignados", asignadosParcelable::class.java) ?: arrayListOf()

        // Asegurarnos de que los pasos estén ordenados
        pasosOriginales.sortBy { it.numero }

        // Initialize the list of modified steps with the same size as the original steps
        pasosModificados = ArrayList(Collections.nCopies(pasosOriginales.size, null))

        // Mostrar el paso actual
        updatePaso()

        // Configurar botones
        btnAnteriorPaso.setOnClickListener {
            if (currentPasoIndex > 0) {
                guardarCambiosPasoActual()
                currentPasoIndex--
                updatePaso()
            }
        }

        btnSiguientePaso.setOnClickListener {
            if (currentPasoIndex < pasosOriginales.size - 1) {
                guardarCambiosPasoActual()
                currentPasoIndex++
                updatePaso()
            }
        }

        btnGuardarTarea.setOnClickListener {
            guardarCambiosPasoActual()
            compararDatosYContinuar()
        }

        btnVolver.setOnClickListener {
            finish()
        }

        ivImagenPaso.setOnClickListener {
            seleccionarImagen()
        }
    }

    private fun updatePaso() {
        val paso = pasosOriginales[currentPasoIndex]
        tvNumeroPaso.text = "Paso ${paso.numero}"
        etDescripcionPaso.setText(paso.descripcion)

        Glide.with(this)
            .load(paso.uri)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(ivImagenPaso)

        // Configurar visibilidad de botones
        btnAnteriorPaso.visibility = if (currentPasoIndex > 0) Button.VISIBLE else Button.GONE
        btnSiguientePaso.visibility = if (currentPasoIndex < pasosOriginales.size - 1) Button.VISIBLE else Button.GONE
    }

    private fun guardarCambiosPasoActual() {
        val descripcionActual = etDescripcionPaso.text.toString()
        val imagenActual = pasosOriginales[currentPasoIndex].uri

        // Comparar datos para ver si han cambiado
        if (descripcionActual != pasosOriginales[currentPasoIndex].descripcion || imagenActual != pasosOriginales[currentPasoIndex].uri) {
            pasosModificados[currentPasoIndex] = PasoTarea(
                pasosOriginales[currentPasoIndex].numero,
                descripcionActual,
                imagenActual
            )
        }
    }

    private fun compararDatosYContinuar() {
        // Comparar los pasos originales con los modificados para determinar qué actualizar
        val pasosFinales = pasosModificados.mapIndexed { index, paso ->
            paso?.takeIf {
                it.descripcion != pasosOriginales[index].descripcion || it.uri != pasosOriginales[index].uri
            }
        }

        // Enviar los datos a la siguiente actividad
        val intent = Intent(this, ModificarAlumnosActivity::class.java)
        intent.putExtra("tareaID", tareaID)
        intent.putExtra("titulo", titulo)
        intent.putExtra("descripcion", descripcion)
        intent.putExtra("imagen", imagen)
        intent.putParcelableArrayListExtra("pasos", ArrayList(pasosFinales.filterNotNull()))
        intent.putParcelableArrayListExtra("asignados", asignados)
        startActivity(intent)
    }

    private fun seleccionarImagen() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            val uri = data?.data ?: return
            pasosOriginales[currentPasoIndex] = pasosOriginales[currentPasoIndex].copy(uri = uri.toString())
            Glide.with(this).load(uri).into(ivImagenPaso)
        }
    }

    companion object {
        private const val REQUEST_CODE_SELECT_IMAGE = 1001
    }
}
