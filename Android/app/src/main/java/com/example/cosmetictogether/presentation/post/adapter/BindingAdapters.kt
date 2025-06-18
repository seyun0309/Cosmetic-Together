package com.example.cosmetictogether.presentation.post.adapter

import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.cosmetictogether.R

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("app:srcCompat")
    fun loadImage(view: ImageView, imageUrl: String?) {
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(view.context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(view)
        } else {
            view.setImageResource(R.drawable.placeholder_image)
        }
    }

    @JvmStatic
    @BindingAdapter("app:isVisible")
    fun setIsVisible(view: View, isVisible: Boolean) {
        view.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("uploadTime")
    fun setUploadTime(textView: TextView, postTime: String?) {
        if (postTime.isNullOrEmpty()) {
            textView.text = textView.context.getString(R.string.unknown_time)
            return
        }
        try {
            val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.getDefault())
            dateFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
            val postDate = dateFormat.parse(postTime)

            postDate?.let {
                val currentTime = java.util.Date()
                val diffInMillis = currentTime.time - it.time
                val diffInMinutes = diffInMillis / 60000

                textView.text = when {
                    diffInMinutes < 1 -> textView.context.getString(R.string.just_now)
                    diffInMinutes < 60 -> textView.context.getString(R.string.minutes_ago, diffInMinutes)
                    diffInMinutes < 1440 -> textView.context.getString(R.string.hours_ago, diffInMinutes / 60)
                    else -> textView.context.getString(R.string.days_ago, diffInMinutes / 1440)
                }
            } ?: run {
                textView.text = textView.context.getString(R.string.unknown_time)
            }
        } catch (e: Exception) {
            textView.text = textView.context.getString(R.string.unknown_time)
        }
    }

    @JvmStatic
    @BindingAdapter("app:gridImages")
    fun setGridImages(gridLayout: GridLayout, images: List<String>?) {
        gridLayout.removeAllViews()
        if (images.isNullOrEmpty()) {
            gridLayout.visibility = View.GONE
            return
        }

        val context = gridLayout.context
        gridLayout.visibility = View.VISIBLE
        gridLayout.columnCount = 2

        val sizeInDp = 150 // 기본 크기
        val marginInDp = 4
        val density = context.resources.displayMetrics.density
        val sizePx = (sizeInDp * density).toInt()
        val marginPx = (marginInDp * density).toInt()

        fun createImageView(): ImageView {
            return ImageView(context).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = sizePx
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(marginPx, marginPx, marginPx, marginPx)
                }
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }

        when (images.size) {
            1 -> {
                val imageView = createImageView().apply {
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = gridLayout.width
                        height = sizePx * 2
                        columnSpec = GridLayout.spec(0, 2)
                        setMargins(marginPx, marginPx, marginPx, marginPx)
                    }
                }
                Glide.with(context).load(images[0]).into(imageView)
                gridLayout.addView(imageView)
            }
            2 -> {
                images.forEach {
                    val imageView = createImageView()
                    Glide.with(context).load(it).into(imageView)
                    gridLayout.addView(imageView)
                }
            }
            3 -> {
                // 첫 번째 이미지는 전체 가로, 아래 2장은 1열씩
                val top = createImageView().apply {
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = sizePx
                        columnSpec = GridLayout.spec(0, 2)
                        setMargins(marginPx, marginPx, marginPx, marginPx)
                    }
                }
                Glide.with(context).load(images[0]).into(top)
                gridLayout.addView(top)

                for (i in 1..2) {
                    val imageView = createImageView().apply {
                        layoutParams.height = sizePx / 2
                    }
                    Glide.with(context).load(images[i]).into(imageView)
                    gridLayout.addView(imageView)
                }
            }
            else -> { // 4장 이상은 2x2 정사각형으로 최대 4장만
                images.take(4).forEach {
                    val imageView = createImageView()
                    Glide.with(context).load(it).into(imageView)
                    gridLayout.addView(imageView)
                }
            }
        }
    }

}
