package com.aboelatta.universalMediaPreview

import android.content.Context
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide

internal fun ImageView.loadImageFromUrl(url: String?) {
    val progressDrawable = createProgressDrawable(this.context)

    Glide.with(this.context)
        .load(url)
        .error(R.drawable.ic_picture)
        .placeholder(progressDrawable)

        .into(this)
}

internal fun createProgressDrawable(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 5f
        centerRadius = 30f
        setColorSchemeColors(ContextCompat.getColor(context, R.color.black_overlay))
        start()
    }
}

