package com.example.granaccess.models

data class Material(
    val material: String = "",  // Nombre del material (e.g., "bolígrafo")
    val color: String = "",     // Color del material (e.g., "amarillo")
    val cantidad: Int = 0       // Cantidad asignada (e.g., 2)
) {
    // Puedes añadir funciones si necesitas lógica relacionada con los materiales
    override fun toString(): String {
        return "$material ($color): $cantidad"
    }
}
