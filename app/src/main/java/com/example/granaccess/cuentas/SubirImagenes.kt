package com.example.granaccess.cuentas

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.granaccess.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class SubirImagenes : AppCompatActivity() {

    private lateinit var storage: FirebaseStorage
    private lateinit var db: FirebaseFirestore
    private val imageUris = mutableListOf<Uri>() // Lista de imágenes seleccionadas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.subir_imagenes)

        storage = FirebaseStorage.getInstance()
        db = FirebaseFirestore.getInstance()

        val btnSubir = findViewById<Button>(R.id.btnSubirImagenes)
        btnSubir.setOnClickListener {
            val userId = intent.getStringExtra("userId") ?: return@setOnClickListener
            subirImagenes(userId)
        }
    }

    private fun subirImagenes(userId: String) {
        val storageRef = storage.reference.child("usuarios/$userId/")
        val urls = mutableListOf<String>()

        imageUris.forEachIndexed { index, uri ->
            val imageRef = storageRef.child("imagen${index + 1}.png")
            imageRef.putFile(uri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { url ->
                        urls.add(url.toString())
                        if (urls.size == imageUris.size) {
                            guardarUrlsEnFirestore(userId, urls)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al subir imagen: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun guardarUrlsEnFirestore(userId: String, urls: List<String>) {
        db.collection("usuarios").document(userId)
            .update("secuencia", urls)
            .addOnSuccessListener {
                Toast.makeText(this, "Secuencia actualizada correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar secuencia: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}