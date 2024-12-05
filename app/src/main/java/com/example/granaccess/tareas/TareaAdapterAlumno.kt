package com.example.granaccess.tareas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.granaccess.R

class TareaAdapterAlumno(
    private val tareas: List<TareaMostrar>,
    private val currentUser: String, // Usuario actual
    private val onTareaClick: (TareaMostrar) -> Unit
) : RecyclerView.Adapter<TareaAdapterAlumno.TareaViewHolder>() {

    class TareaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: LinearLayout = view.findViewById(R.id.itemTareaContainer)
        val titulo: TextView = view.findViewById(R.id.tareaTitulo)
        val descripcion: TextView = view.findViewById(R.id.tareaDescripcion)
        val estado: TextView = view.findViewById(R.id.tareaEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tarea_alumno, parent, false)
        return TareaViewHolder(view)
    }

    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        val tarea = tareas[position]

        holder.titulo.text = tarea.titulo
        holder.descripcion.text = tarea.descripcion

        val esCompletado = tarea.asignados[currentUser] == true
        val estadoTexto = if (esCompletado) "Completado" else "Pendiente"
        val estadoColor = if (esCompletado) R.color.green else R.color.deepBlueText


        // Cambiar el color del título y del estado
        holder.titulo.setTextColor(ContextCompat.getColor(holder.itemView.context, estadoColor))
        holder.estado.text = estadoTexto
        holder.estado.setTextColor(ContextCompat.getColor(holder.itemView.context, estadoColor))

        // Configurar clics solo si está pendiente
        holder.container.isClickable = !esCompletado
        if (!esCompletado) {
            holder.container.setBackgroundResource(R.drawable.tarea_pendiente)
            holder.container.setOnClickListener {
                onTareaClick(tarea)
            }
        } else {
            holder.container.setBackgroundResource(R.drawable.tarea_completada)
            holder.container.setOnClickListener(null) // Deshabilitar clic
        }
    }


    override fun getItemCount(): Int = tareas.size
}

