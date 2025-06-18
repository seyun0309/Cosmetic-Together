package com.example.cosmetictogether.presentation.post.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cosmetictogether.R
import com.example.cosmetictogether.data.model.PostRecentResponse
import com.example.cosmetictogether.databinding.ItemPostBinding

class PostAdapter(
    private val onItemClick: (PostRecentResponse) -> Unit,
) : ListAdapter<PostRecentResponse, PostAdapter.PostViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)

        val imageViews = listOf(
            holder.binding.image1,
            holder.binding.image2,
            holder.binding.image3,
            holder.binding.image4
        )

        // 모두 초기화
        imageViews.forEach { it.visibility = View.GONE }

        post.boardUrl?.take(4)?.forEachIndexed { index, url ->
            val imageView = imageViews[index]
            imageView.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(url)
                .centerCrop()
                .into(imageView)
        }

        holder.binding.imageGrid.visibility = if (post.boardUrl.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    inner class PostViewHolder(val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: PostRecentResponse) {
            binding.post = post
            binding.root.setOnClickListener { onItemClick(post) }

            // 프로필 이미지 로드
            Glide.with(binding.profileImage.context)
                .load(post.profileUrl)
                .circleCrop()
                .into(binding.profileImage)

            binding.executePendingBindings()
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<PostRecentResponse>() {
        override fun areItemsTheSame(oldItem: PostRecentResponse, newItem: PostRecentResponse): Boolean {
            return oldItem.boardId == newItem.boardId
        }

        override fun areContentsTheSame(oldItem: PostRecentResponse, newItem: PostRecentResponse): Boolean {
            return oldItem == newItem
        }
    }
}