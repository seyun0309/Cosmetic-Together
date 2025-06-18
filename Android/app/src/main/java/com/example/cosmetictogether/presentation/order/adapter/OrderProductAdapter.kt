package com.example.cosmetictogether.presentation.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cosmetictogether.data.model.OrderProductResponse
import com.example.cosmetictogether.databinding.ItemOrderProductBinding

class ProductAdapter : ListAdapter<OrderProductResponse, ProductAdapter.ProductViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<OrderProductResponse>() {
            override fun areItemsTheSame(oldItem: OrderProductResponse, newItem: OrderProductResponse): Boolean {
                return oldItem.productName == newItem.productName
            }

            override fun areContentsTheSame(oldItem: OrderProductResponse, newItem: OrderProductResponse): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemOrderProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
    }

    class ProductViewHolder(private val binding: ItemOrderProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: OrderProductResponse) {
            binding.product = product
            binding.executePendingBindings()
        }
    }
}