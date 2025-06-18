package com.example.cosmetictogether.presentation.form.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("android:text")
fun setTextFromInt(view: TextView, value: Int) {
    view.text = value.toString() // 또는 String.format()을 사용하여 포맷할 수 있습니다
}