package com.example.granaccess

import java.io.Serializable

data class Tarea(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val pasos: Map<Int, String>
)
