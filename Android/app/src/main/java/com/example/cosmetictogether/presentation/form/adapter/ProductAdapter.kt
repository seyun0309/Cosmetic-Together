package com.example.cosmetictogether.presentation.form.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cosmetictogether.R
import com.example.cosmetictogether.data.model.Product
import com.example.cosmetictogether.databinding.ItemFormWriteProductBinding

class ProductAdapter : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    var onEditClick: ((Product) -> Unit)? = null
    var onDeleteClick: ((Product) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemFormWriteProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
    }

    inner class ProductViewHolder(private val binding: ItemFormWriteProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.tvProductName.text = product.productName
            binding.tvProductPrice.text = "${product.price}원"
            binding.tvProductStock.text = "재고: ${product.stock}개"

            // 상품 이미지 설정
            if (product.imageUri != null) {
                binding.ivProductImage.setImageURI(product.imageUri)
            } else {
                binding.ivProductImage.setImageResource(R.drawable.baseline_child_care_24) // 기본 이미지
            }

            binding.btnEditProduct.setOnClickListener {
                onEditClick?.invoke(product)
            }

            binding.btnDeleteProductMethod.setOnClickListener {
                onDeleteClick?.invoke(product)
            }

        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.productName == newItem.productName
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}
