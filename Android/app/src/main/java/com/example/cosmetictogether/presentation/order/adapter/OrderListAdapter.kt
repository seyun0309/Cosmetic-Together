package com.example.cosmetictogether.presentation.order.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cosmetictogether.data.model.Orders
import com.example.cosmetictogether.databinding.ItemOrderSummaryBinding

class OrderListAdapter (
    private var orders: List<Orders>,
    private val onOrderDetailClick: (Long, Long) -> Unit) :
    RecyclerView.Adapter<OrderListAdapter.OrderListViewHolder>() {

    class OrderListViewHolder(private val binding: ItemOrderSummaryBinding, private val onOrderDetailClick: (Long, Long) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        // Bind 데이터를 XML에 연결
        fun bind(order: Orders) {
            binding.order = order // XML에서 'order' 변수로 참조
            binding.executePendingBindings()

            // btnOrderDetail 클릭 이벤트 설정
            binding.root.setOnClickListener {
                onOrderDetailClick(order.orderId, order.formId)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemOrderSummaryBinding.inflate(layoutInflater, parent, false)
        return OrderListViewHolder(binding, onOrderDetailClick)
    }

    override fun onBindViewHolder(holder: OrderListViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order) // 데이터 바인딩
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(orders: List<Orders>) {
        this.orders = orders
        notifyDataSetChanged()
    }
}