package com.example.cosmetictogether.presentation.mypage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cosmetictogether.data.model.FollowerListRequest
import com.example.cosmetictogether.databinding.ItemFollowerBinding

class FollowerAdapter : RecyclerView.Adapter<FollowerAdapter.FollowerViewHolder>() {

    private val followerList = mutableListOf<FollowerListRequest>()

    fun submitList(list: List<FollowerListRequest>) {
        followerList.clear()
        followerList.addAll(list)
        notifyDataSetChanged()
    }

    inner class FollowerViewHolder(private val binding: ItemFollowerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FollowerListRequest) {
            binding.nickname.text = item.nickname
            Glide.with(binding.profileImage.context)
                .load(item.profileUrl)
                .into(binding.profileImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFollowerBinding.inflate(inflater, parent, false)
        return FollowerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FollowerViewHolder, position: Int) {
        holder.bind(followerList[position])
    }

    override fun getItemCount(): Int = followerList.size
}