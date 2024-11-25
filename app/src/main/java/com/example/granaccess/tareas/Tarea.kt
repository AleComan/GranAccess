package com.example.granaccess.tareas

data class Tarea(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val pasos: Map<Int, String>
)
