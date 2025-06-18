package com.example.cosmetictogether.presentation.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cosmetictogether.databinding.ItemRecentSearchBinding

class RecentSearchAdapter(private val items: List<String>) :
    RecyclerView.Adapter<RecentSearchAdapter.RecentSearchViewHolder>() {

    inner class RecentSearchViewHolder(val binding: ItemRecentSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.searchKeywordText.text = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentSearchViewHolder {
        val binding = ItemRecentSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentSearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentSearchViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}