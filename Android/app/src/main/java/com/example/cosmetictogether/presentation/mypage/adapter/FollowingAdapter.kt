package com.example.cosmetictogether.presentation.mypage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cosmetictogether.R
import com.example.cosmetictogether.data.model.FollowingListRequest
import com.example.cosmetictogether.databinding.ItemFollowingBinding

class FollowingAdapter(
    private val onFollowToggleClick: (FollowingListRequest) -> Unit
) : RecyclerView.Adapter<FollowingAdapter.FollowingViewHolder>() {

    private val followingList = mutableListOf<FollowingListRequest>()

    fun submitList(list: List<FollowingListRequest>) {
        followingList.clear()
        followingList.addAll(list)
        notifyDataSetChanged()
    }

    inner class FollowingViewHolder(private val binding: ItemFollowingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FollowingListRequest) {
            binding.nickname.text = item.nickname
            Glide.with(binding.profileImage.context)
                .load(item.profileUrl)
                .into(binding.profileImage)

            binding.followButton.text = if (item.following) "언팔로잉" else "팔로잉"
            binding.followButton.setBackgroundResource(
                if (item.following) R.drawable.baseline_unfollow
                else R.drawable.baseline_follow
            )
            binding.followButton.setOnClickListener {
                onFollowToggleClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFollowingBinding.inflate(inflater, parent, false)
        return FollowingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FollowingViewHolder, position: Int) {
        holder.bind(followingList[position])
    }

    override fun getItemCount(): Int = followingList.size
}