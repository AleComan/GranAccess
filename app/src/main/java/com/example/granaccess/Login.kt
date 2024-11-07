package com.example.granaccess

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge

class Login : ComponentActivity(){
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login)
        val changeLogin = findViewById<Button>(R.id.toStudentLogin)
        val login = findViewById<Button>(R.id.loginButton)

        changeLogin.setOnClickListener {
            val intent = Intent(this, LoginAlumnos::class.java)
            startActivity(intent)
        }

        login.setOnClickListener {
            val intent = Intent(this, MenuTareas::class.java)
            startActivity(intent)
        }

    }
}