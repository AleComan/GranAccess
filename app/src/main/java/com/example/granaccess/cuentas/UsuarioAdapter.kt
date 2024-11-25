package com.example.granaccess.cuentas

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.granaccess.R

class UsuarioAdapter(
    private val usuarios: List<Usuario>,
    private val onClick: (Usuario) -> Unit
) : RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    inner class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.ivIcono)
        val textView: TextView = itemView.findViewById(R.id.tvUsername)

        fun bind(usuario: Usuario) {
            textView.text = usuario.username

            // Verifica si el atributo `icono` es nulo, vacío o no válido
            if (usuario.icono.isNullOrEmpty()) {
                // Cargar placeholder si no hay imagen válida
                imageView.setImageResource(R.drawable.placeholder)
            } else {
                // Cargar la imagen desde la URL usando Glide
                Glide.with(itemView.context)
                    .load(usuario.icono) // No necesitas Uri.parse si `icono` ya es una URL
                    .placeholder(R.drawable.placeholder) // Imagen mientras carga
                    .error(R.drawable.placeholder)       // Imagen en caso de error
                    .into(imageView)
            }

            // Configurar clic para el usuario
            itemView.setOnClickListener {
                onClick(usuario)
            }
        }
    }

    @SuppressLint("ResourceType")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usuario, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        holder.bind(usuarios[position])
    }

    override fun getItemCount(): Int = usuarios.size
}
