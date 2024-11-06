package com.example.granaccess

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import kotlinx.coroutines.*

class LoginAlumnos : ComponentActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_alumnos)

        // Botones de cambio de vista

        val backButton = findViewById<Button>(R.id.BackToMain)
        val changeLogin = findViewById<Button>(R.id.ToNormalLogin)

        // Botones del login para alumnos

        val botonMario = findViewById<ImageButton>(R.id.imageMario)
        val botonLuigi = findViewById<ImageButton>(R.id.imageLuigi)
        val botonPeach = findViewById<ImageButton>(R.id.imagePeach)
        val botonYoshi = findViewById<ImageButton>(R.id.imageYoshi)
        val botonDaisy = findViewById<ImageButton>(R.id.imageDaisy)
        val botonToad = findViewById<ImageButton>(R.id.imageToad)

        // Lista para almacenar el orden de los botones presionados
        val ordenBotones = mutableListOf<ImageButton>()

        // Combinación correcta de botones
        val combinacionCorrecta = listOf(botonPeach, botonYoshi)

        fun restoreOpacity(){
            botonMario.alpha = 1.0f
            botonLuigi.alpha = 1.0f
            botonPeach.alpha = 1.0f
            botonYoshi.alpha = 1.0f
            botonDaisy.alpha = 1.0f
            botonToad.alpha = 1.0f
        }

        fun verificarCombinacion(){
            if (ordenBotones == combinacionCorrecta){
                val intentChange = Intent(this, VistaAlumno::class.java)
                startActivity(intentChange)
            } else {
                Toast.makeText(this, "Combinación incorrecta", Toast.LENGTH_SHORT).show()
                GlobalScope.launch {
                    delay(500)
                    withContext(Dispatchers.Main) {
                        restoreOpacity()
                    }
                }
            }
            ordenBotones.clear()
        }

        backButton.setOnClickListener {
            val intentBack = Intent(this, MainActivity::class.java)
            startActivity(intentBack)
        }

        changeLogin.setOnClickListener {
            val intentChange = Intent(this, Login::class.java)
            startActivity(intentChange)
        }

        botonMario.setOnClickListener {
            ordenBotones.add(botonMario)
            botonMario.alpha = 0.5f
            if (ordenBotones.size == combinacionCorrecta.size) verificarCombinacion()
        }

        botonLuigi.setOnClickListener {
            ordenBotones.add(botonLuigi)
            botonLuigi.alpha = 0.5f
            if (ordenBotones.size == combinacionCorrecta.size) verificarCombinacion()
        }

        botonPeach.setOnClickListener {
            ordenBotones.add(botonPeach)
            botonPeach.alpha = 0.5f
            if (ordenBotones.size == combinacionCorrecta.size) verificarCombinacion()
        }

        botonDaisy.setOnClickListener {
            ordenBotones.add(botonDaisy)
            botonDaisy.alpha = 0.5f
            if (ordenBotones.size == combinacionCorrecta.size) verificarCombinacion()
        }

        botonYoshi.setOnClickListener {
            ordenBotones.add(botonYoshi)
            botonYoshi.alpha = 0.5f
            if (ordenBotones.size == combinacionCorrecta.size) verificarCombinacion()
        }

        botonToad.setOnClickListener {
            ordenBotones.add(botonToad)
            botonToad.alpha = 0.5f
            if (ordenBotones.size == combinacionCorrecta.size) verificarCombinacion()
        }
    }
}