package com.example.cosmetictogether.presentation.form.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cosmetictogether.data.model.FormSummaryResponse
import com.example.cosmetictogether.databinding.ItemFormBinding

class FormAdapter : ListAdapter<FormSummaryResponse, FormAdapter.FormViewHolder>(FormDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemFormBinding.inflate(layoutInflater, parent, false)
        return FormViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val formItem = getItem(position)
        holder.bind(formItem)
    }

    class FormViewHolder(private val binding: ItemFormBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(formItem: FormSummaryResponse) {
            binding.formItem = formItem
            binding.executePendingBindings()
        }
    }

    class FormDiffCallback : DiffUtil.ItemCallback<FormSummaryResponse>() {
        override fun areItemsTheSame(oldItem: FormSummaryResponse, newItem: FormSummaryResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FormSummaryResponse, newItem: FormSummaryResponse): Boolean {
            return oldItem == newItem
        }
    }
}
