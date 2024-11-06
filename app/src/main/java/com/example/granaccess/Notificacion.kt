package com.example.granaccess

import java.io.Serializable

data class Notificacion(
    val id: String,
    val asunto: String,
    val descripcion: String,
    var leida: Boolean = false // false por defecto (no leída)
) : Serializable
