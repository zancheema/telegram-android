package com.zancheema.android.telegram.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.zancheema.android.telegram.R

object CommonBindings {
    @BindingAdapter("profile_image_url")
    @JvmStatic
    fun setProfileImageUrl(imageView: ImageView, url: String) {
        Glide
            .with(imageView.context)
            .load(url)
            .placeholder(R.drawable.ic_baseline_person_24)
            .error(R.drawable.ic_baseline_person_24)
            .into(imageView)
    }
}