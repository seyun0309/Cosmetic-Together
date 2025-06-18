package com.example.cosmetictogether.presentation.post.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cosmetictogether.data.model.Comment
import com.example.cosmetictogether.databinding.ItemCommentBinding

class CommentAdapter(private val comments: List<Comment>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCommentBinding.inflate(inflater, parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.binding.comment = comments[position]
    }

    override fun getItemCount(): Int = comments.size
}