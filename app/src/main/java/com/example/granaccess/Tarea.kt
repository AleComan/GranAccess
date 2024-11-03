package com.example.granaccess

import java.io.Serializable

data class Tarea(
    val titulo: String,
    val descripcion: String,
    val pasos: Map<Int, String>
) : Serializable
