package com.example.granaccess.cuentas

data class Usuario(
    val id: String = "",
    val username: String = "",
    val icono: String = "",
    val role: String = "",
    val imgPref: Boolean = false,       // Si la preferencia es imagen (true) o texto (false)
    val secuencia: List<String> = emptyList()
)
