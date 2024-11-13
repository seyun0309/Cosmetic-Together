package com.example.cosmetictogether.presentation.form.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cosmetictogether.data.model.Delivery
import com.example.cosmetictogether.databinding.ItemFormWriteDeliveryBinding

class DeliveryAdapter : ListAdapter<Delivery, DeliveryAdapter.DeliveryViewHolder>(DeliveryDiffCallback()) {

    var onDeleteClick: ((Delivery) -> Unit)? = null // 삭제 리스너 추가

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryViewHolder {
        val binding = ItemFormWriteDeliveryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeliveryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
        val delivery = getItem(position)
        holder.bind(delivery)
    }

    inner class DeliveryViewHolder(private val binding: ItemFormWriteDeliveryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(delivery: Delivery) {
            binding.tvDeliveryMethod.text = delivery.deliveryOption
            binding.tvDeliveryCost.text = "${delivery.deliveryCost}원"

            binding.btnDeleteDeliveryMethod.setOnClickListener {
                onDeleteClick?.invoke(delivery) // 삭제 리스너 호출
            }
        }
    }

    class DeliveryDiffCallback : DiffUtil.ItemCallback<Delivery>() {
        override fun areItemsTheSame(oldItem: Delivery, newItem: Delivery): Boolean {
            return oldItem.deliveryOption == newItem.deliveryOption
        }

        override fun areContentsTheSame(oldItem: Delivery, newItem: Delivery): Boolean {
            return oldItem == newItem
        }
    }
}
