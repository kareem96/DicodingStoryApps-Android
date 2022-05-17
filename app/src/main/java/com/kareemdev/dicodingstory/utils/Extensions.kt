package com.kareemdev.dicodingstory.utils

import android.animation.ObjectAnimator
import android.view.View
import android.widget.TextView
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun View.animateVisibility(isVisible: Boolean, duration: Long = 400) {
    ObjectAnimator
        .ofFloat(this, View.ALPHA, if (isVisible) 1f else 0f)
        .setDuration(duration)
        .start()
}


fun TextView.setLocalDateFormat(timestamp: String) {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    val date = sdf.parse(timestamp) as Date

    val formattedDate = DateFormat.getDateInstance(DateFormat.FULL).format(date)
    this.text = formattedDate
}