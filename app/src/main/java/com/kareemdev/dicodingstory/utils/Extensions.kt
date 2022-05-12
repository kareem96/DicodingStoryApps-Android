package com.kareemdev.dicodingstory.utils

import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.kareemdev.dicodingstory.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun View.animateVisibility(isVisible: Boolean, duration: Long = 400) {
    ObjectAnimator
        .ofFloat(this, View.ALPHA, if (isVisible) 1f else 0f)
        .setDuration(duration)
        .start()
}

fun ImageView.setImageFromUrl(context: Context, url: String) {
    Glide
        .with(context)
        .load(url)
        .placeholder(R.drawable.image_loading_placeholder)
        .error(R.drawable.image_load_error)
        .into(this)
}

fun TextView.setLocalDateFormat(timestamp: String) {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    val date = sdf.parse(timestamp) as Date

    val formattedDate = DateFormat.getDateInstance(DateFormat.FULL).format(date)
    this.text = formattedDate
}