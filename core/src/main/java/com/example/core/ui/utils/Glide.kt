package com.example.core.ui.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.core.R


fun ImageView.loadImageFromUrlPreview(url: String?) {
    Glide.with(this.context)
        .load(url)
        .error(R.drawable.ic_picture)
        .into(this)
}
fun ImageView.loadImageFromUrl(url: String?) {
    val progressDrawable = createProgressDrawable(this.context)

    Glide.with(this.context)
        .load(url)
        .error(R.drawable.ic_picture)
        .placeholder(progressDrawable)

        .into(this)
}

fun ImageView.loadImageFromUrlWithLoading(url: String?) {
    val progressDrawable = createProgressDrawable(this.context)

    Glide.with(this.context)
        .load(url)
        .error(R.drawable.ic_picture)
        .placeholder(progressDrawable)
        .into(this)
}


fun ImageView.loadImageFromUrl(url: Uri?) {
    val progressDrawable = createProgressDrawable(this.context)

    Glide.with(this.context)
        .load(url)
        .error(R.drawable.ic_picture)
        .placeholder(progressDrawable)
        .into(this)
}

fun ImageView.loadImageFromUrlWithResize(url: String?,height:Int = 200,width:Int = 200) {
    Glide.with(this.context)
        .load(url)
        .apply(RequestOptions().override(width, height))
        .error(R.drawable.ic_picture)
        .into(this)
}

fun ImageView.loadCircleImageFromUrl(url: String?,height:Int = 200,width:Int = 200) {
    val progressDrawable = createProgressDrawable(this.context)

    Glide.with(this.context)
        .load(url)
        .apply(RequestOptions().override(width, height).circleCrop())
        .error(R.drawable.ic_profile_placeholder)
        .placeholder(progressDrawable)

        .into(this)
}

fun ImageView.loadImageFromDrawable(context: Context, url: Int) {
    Glide.with(context)
        .load(url)
        .into(this)
}

fun createProgressDrawable(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 5f
        centerRadius = 30f
        setColorSchemeColors(ContextCompat.getColor(context, R.color.colorPrimary))
        start()
    }
}

