package com.example.cosmetictogether.presentation.order.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cosmetictogether.data.api.OrderRetrofitInterface
import com.example.cosmetictogether.data.api.RetrofitClient
import com.example.cosmetictogether.data.model.OrderFormResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderViewModel: ViewModel() {

    private val _formId = MutableLiveData<Long>()
    val formId: LiveData<Long> get() = _formId

    private val _orderDate = MutableLiveData<String>()
    val orderDate: LiveData<String> get() = _orderDate

    private val _thumbnail = MutableLiveData<String>()
    val thumbnail: LiveData<String> get() = _thumbnail

    private val _title = MutableLiveData<String>()
    val title: LiveData<String> get() = _title

    private val _totalPrice = MutableLiveData<Int>()
    val totalPrice: LiveData<Int> get() = _totalPrice

    private val _orderItem = MutableLiveData<OrderFormResponse>()
    val formItem: LiveData<OrderFormResponse> get() = _orderItem

    private val _orderData = MutableLiveData<List<OrderFormResponse>>()
    val formData: LiveData<List<OrderFormResponse>> get() = _orderData

    private val _orderForms = MutableLiveData<List<OrderFormResponse>>()
    val orderForms: LiveData<List<OrderFormResponse>> get() = _orderForms

    private val orderApi: OrderRetrofitInterface =
        RetrofitClient.getInstance().create(OrderRetrofitInterface::class.java)

    // 주문한 폼 리스트 조회
    fun loadOrderForm(token: String) {
        orderApi.getOrderForm(token).enqueue(object : Callback<List<OrderFormResponse>> {
            override fun onResponse(call: Call<List<OrderFormResponse>>, response: Response<List<OrderFormResponse>>) {
                if(response.isSuccessful) {
                    response.body()?.let { data ->
                        if (data.isNotEmpty()) {
                            _orderForms.value = response.body()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<OrderFormResponse>>, t: Throwable) {
                Log.e("LoadOrderForm", "Error: ${t.message}")
            }

        })
    }
}