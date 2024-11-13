package com.example.cosmetictogether.presentation.form.adapter

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("imageUrl")
    fun loadImage(view: ImageView, imageUrl: String?) {
        Glide.with(view.context)
            .load(imageUrl)
            .into(view)
    }

    @JvmStatic
    @BindingAdapter("imageUri")
    fun loadImage(view: ImageView, imageUri: Uri?) {
        if (imageUri != null) {
            Glide.with(view.context)
                .load(imageUri.toString())
                .into(view)
        }
    }
}