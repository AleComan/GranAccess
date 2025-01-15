package com.example.granaccess.tareas

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.granaccess.R
import java.util.*

class PasoTareaAdapter(
    private val pasos: MutableList<PasoTarea>,
    private val context: Context
) : RecyclerView.Adapter<PasoTareaAdapter.PasoViewHolder>() {

    inner class PasoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPasoNumero: TextView = itemView.findViewById(R.id.tvPasoNumero)
        val tvPasoDescripcion: TextView = itemView.findViewById(R.id.tvPasoDescripcion)
        val ivReorder: ImageView = itemView.findViewById(R.id.ivReorder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_paso, parent, false)
        return PasoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PasoViewHolder, position: Int) {
        val paso = pasos[position]
        holder.tvPasoNumero.text = "Paso ${paso.numero}"
        holder.tvPasoDescripcion.text = paso.descripcion
    }

    override fun getItemCount(): Int = pasos.size

    /**
     * Mueve un elemento de una posición a otra dentro de la lista.
     */
    fun moveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(pasos, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(pasos, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }
}
