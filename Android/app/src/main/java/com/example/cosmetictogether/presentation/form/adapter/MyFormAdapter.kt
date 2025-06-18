package com.example.cosmetictogether.presentation.form.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cosmetictogether.data.model.MyFormResponse
import com.example.cosmetictogether.databinding.ItemMyformBinding

class MyFormAdapter(
    private var forms: List<MyFormResponse>,
    private val onFormDetailClick: (Long) -> Unit,
    private val onOrderListClick: (Long) -> Unit) :
    RecyclerView.Adapter<MyFormAdapter.MyFormViewHolder>(){

    // 클릭 리스너를 위한 변수
    private var onItemClickListener: ((Long) -> Unit)? = null

    // 외부에서 클릭 리스너를 설정할 수 있도록 메서드 제공
    fun setOnItemClickListener(listener: (Long) -> Unit) {
        onItemClickListener = listener
    }

    class MyFormViewHolder(private val binding: ItemMyformBinding, private val onFormDetailClick: (Long) -> Unit, private val onOrderListClick: (Long) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        // Bind 데이터를 XML에 연결
        fun bind(form: MyFormResponse) {
            binding.form = form
            binding.executePendingBindings()

            // 버튼 클릭 시 해당 폼 ID를 전달
            binding.btnFormDetail.setOnClickListener {
                onFormDetailClick(form.formId)
            }

            // btnOrderDetail 클릭 이벤트 설정
            binding.btnOrderList.setOnClickListener {
                onOrderListClick(form.formId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFormViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemMyformBinding.inflate(layoutInflater, parent, false)
        return MyFormViewHolder(binding, onFormDetailClick, onOrderListClick)
    }

    override fun onBindViewHolder(holder: MyFormViewHolder, position: Int) {
        val order = forms[position]
        holder.bind(order) // 데이터 바인딩
    }

    override fun getItemCount(): Int {
        return forms.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newForms: List<MyFormResponse>) {
        this.forms = newForms
        notifyDataSetChanged()
    }
}