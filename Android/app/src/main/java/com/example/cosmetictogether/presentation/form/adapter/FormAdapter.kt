package com.example.cosmetictogether.presentation.form.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cosmetictogether.data.model.FormSummaryResponse
import com.example.cosmetictogether.databinding.ItemFormBinding

class FormAdapter : ListAdapter<FormSummaryResponse, FormAdapter.FormViewHolder>(FormDiffCallback()) {

    // 클릭 리스너를 위한 변수
    private var onItemClickListener: ((Long) -> Unit)? = null

    // 외부에서 클릭 리스너를 설정할 수 있도록 메서드 제공
    fun setOnItemClickListener(listener: (Long) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemFormBinding.inflate(layoutInflater, parent, false)
        return FormViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val formItem = getItem(position)
        holder.bind(formItem)

        // 클릭 리스너 설정
        holder.itemView.setOnClickListener {
            formItem.id.let { id ->
                onItemClickListener?.invoke(id) // formItem의 ID를 전달하여 리스너 호출
            }
        }
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
