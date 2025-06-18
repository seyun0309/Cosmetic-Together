package com.example.cosmetictogether.presentation.post.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cosmetictogether.databinding.ItemPostEditPhotoBinding

class PostImageAdapter(
    private val onDeleteClick: (String) -> Unit
) : ListAdapter<String, PostImageAdapter.ImageViewHolder>(DiffCallback()) {

    inner class ImageViewHolder(private val binding: ItemPostEditPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(imageUrl: String) {
            binding.imageUrl = imageUrl

            // 이미지 로딩
            Glide.with(binding.selectedImageView.context)
                .load(imageUrl)
                .centerCrop()
                .into(binding.selectedImageView)

            // 삭제 버튼 이벤트
            binding.deleteImageButton.setOnClickListener {
                onDeleteClick(imageUrl)
            }

            // onDelete에 View.OnClickListener 설정
            binding.onDelete = View.OnClickListener {
                onDeleteClick(imageUrl)
            }

            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemPostEditPhotoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
    }
}