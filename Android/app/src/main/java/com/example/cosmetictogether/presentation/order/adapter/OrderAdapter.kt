package com.example.cosmetictogether.presentation.order.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cosmetictogether.data.model.OrderFormResponse
import com.example.cosmetictogether.databinding.ItemOrderBinding

class OrderAdapter(
    private var orders: List<OrderFormResponse>,
    private val onFormDetailClick: (Long) -> Unit,
    private val onOrderDetailClick: (Long) -> Unit) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    // 클릭 리스너를 위한 변수
    private var onItemClickListener: ((Long) -> Unit)? = null

    // 외부에서 클릭 리스너를 설정할 수 있도록 메서드 제공
    fun setOnItemClickListener(listener: (Long) -> Unit) {
        onItemClickListener = listener
    }

    class OrderViewHolder(private val binding: ItemOrderBinding, private val onFormDetailClick: (Long) -> Unit, private val onOrderDetailClick: (Long) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        // Bind 데이터를 XML에 연결
        fun bind(order: OrderFormResponse) {
            binding.order = order // XML에서 'order' 변수로 참조
            binding.executePendingBindings()

            // 버튼 클릭 시 해당 폼 ID를 전달
            binding.btnFormDetail.setOnClickListener {
                onFormDetailClick(order.formId)
            }

            // btnOrderDetail 클릭 이벤트 설정
            binding.btnOrderDetail.setOnClickListener {
                onOrderDetailClick(order.orderId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemOrderBinding.inflate(layoutInflater, parent, false)
        return OrderViewHolder(binding, onFormDetailClick, onOrderDetailClick)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order) // 데이터 바인딩
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newOrders: List<OrderFormResponse>) {
        this.orders = newOrders
        notifyDataSetChanged()
    }
}