package com.example.granaccess.cuentas

import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide

class ImageAdapter(private val context: Context, private val imageUris: List<Uri>) : BaseAdapter() {

    override fun getCount(): Int {
        return imageUris.size // Total number of images
    }

    override fun getItem(position: Int): Any {
        return imageUris[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView: ImageView
        if (convertView == null) {
            // If it's not recycled, initialize some attributes
            imageView = ImageView(context)
            imageView.layoutParams = AbsListView.LayoutParams(250, 250) // Set size
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setPadding(8, 8, 8, 8)
        } else {
            imageView = convertView as ImageView
        }

        // Load image from the URI
        Glide.with(context)
            .load(imageUris[position])
            .into(imageView)

        return imageView
    }
}
