package com.example.granaccess.tareas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.granaccess.R

class TareaAdapterModificar(
    private val tareas: List<TareaMostrar>,
    private val onTareaClick: (TareaMostrar) -> Unit
) : RecyclerView.Adapter<TareaAdapterModificar.TareaViewHolder>() {

    class TareaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: LinearLayout = view.findViewById(R.id.itemTareaContainer)
        val titulo: TextView = view.findViewById(R.id.tareaTitulo)
        val descripcion: TextView = view.findViewById(R.id.tareaDescripcion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tarea, parent, false)
        return TareaViewHolder(view)
    }

    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        val tarea = tareas[position]

        holder.titulo.text = tarea.titulo
        holder.descripcion.text = tarea.descripcion

        // Cambiar el color del título a azul siempre
        val colorAzul = ContextCompat.getColor(holder.itemView.context, R.color.deepBlueText)
        holder.titulo.setTextColor(colorAzul)

        // Fondo azul para todas las tareas
        holder.container.setBackgroundResource(R.drawable.tarea_pendiente)

        // Configurar el clic para todas las tareas
        holder.container.setOnClickListener {
            onTareaClick(tarea)
        }
    }

    override fun getItemCount(): Int = tareas.size
}
