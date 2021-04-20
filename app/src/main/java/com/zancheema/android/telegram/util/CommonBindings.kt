package com.zancheema.android.telegram.util

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.zancheema.android.telegram.R
import java.text.SimpleDateFormat
import java.util.*

object CommonBindings {
    @BindingAdapter("photo_url")
    @JvmStatic
    fun setPhotoUrl(imageView: ImageView, url: String?) {
        Glide
            .with(imageView.context)
            .load(url)
            .placeholder(R.drawable.ic_baseline_person_24)
            .error(R.drawable.ic_baseline_person_24)
            .into(imageView)
    }

    @BindingAdapter("time_millis")
    @JvmStatic
    fun setTimeMillis(textView: TextView, millis: Long) {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val time = timeFormat.format(Date(millis))
        textView.text = time
    }
}