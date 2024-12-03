package com.example.cosmetictogether.presentation.order.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cosmetictogether.data.api.OrderRetrofitInterface
import com.example.cosmetictogether.data.api.RetrofitClient
import com.example.cosmetictogether.data.model.OrderListResponse
import com.example.cosmetictogether.data.model.Orders
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyFormOrderViewModel : ViewModel() {

    private val _myFormOrders = MutableLiveData<OrderListResponse?>()
    val myFormOrders: MutableLiveData<OrderListResponse?> get() = _myFormOrders

    private val _orders = MutableLiveData<List<Orders>>()

    val orders: LiveData<List<Orders>> get() = _orders
    private val orderApi: OrderRetrofitInterface =
        RetrofitClient.getInstance().create(OrderRetrofitInterface::class.java)

    fun loadOrderList(formId: Long) {
        orderApi.getOrderList(formId).enqueue(object : Callback<OrderListResponse> {
            override fun onResponse(call: Call<OrderListResponse>, response: Response<OrderListResponse>) {
                if(response.isSuccessful) {
                    val orderListResponse = response.body()
                    _myFormOrders.value = orderListResponse
                    _orders.value = orderListResponse?.orders
                }
            }

            override fun onFailure(call: Call<OrderListResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }
}