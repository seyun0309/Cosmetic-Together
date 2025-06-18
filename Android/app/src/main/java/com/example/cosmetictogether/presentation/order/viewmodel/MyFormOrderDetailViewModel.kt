package com.example.cosmetictogether.presentation.order.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cosmetictogether.data.api.OrderRetrofitInterface
import com.example.cosmetictogether.data.api.RetrofitClient
import com.example.cosmetictogether.data.model.APIResponse
import com.example.cosmetictogether.data.model.OrderDetailResponse
import com.example.cosmetictogether.data.model.OrderProductResponse
import com.example.cosmetictogether.data.model.UpdateOrderStatusRequest
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyFormOrderDetailViewModel : ViewModel() {
    private val _orderDetail = MutableLiveData<OrderDetailResponse?>()
    val orderDetail: LiveData<OrderDetailResponse?> get() = _orderDetail

    private val _orderProducts = MutableLiveData<List<OrderProductResponse>>()
    val orderProducts: LiveData<List<OrderProductResponse>> get() = _orderProducts

    private val orderApi: OrderRetrofitInterface =
        RetrofitClient.getInstance().create(OrderRetrofitInterface::class.java)

    fun getOrderDetail(context: Context, token: String, formId: Long) {
        orderApi.getOrderDetail(token, formId).enqueue(object : Callback<OrderDetailResponse> {
            override fun onResponse(call: Call<OrderDetailResponse>, response: Response<OrderDetailResponse>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        // LiveData 업데이트
                        _orderDetail.postValue(data)
                        _orderProducts.postValue(data.orderProducts ?: emptyList())
                    }
                } else {
                    val errorMessage = parseErrorMessage(response)
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<OrderDetailResponse>, t: Throwable) {
                // 네트워크 요청 실패 처리
                Log.e("OrderDetailViewModel", "API call failed: ${t.message}")
            }
        })
    }

    fun updateOrderStatus(
        context: Context,
        formId: Long,
        status: String,
        callback: (Boolean) -> Unit
    ) {
        val request = UpdateOrderStatusRequest(status)
        orderApi.updateOrderStatus(formId, request).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {
                if (response.isSuccessful) {
                    val errorMessage = parseErrorMessage(response)
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    callback(true)
                } else {
                    val errorMessage = parseErrorMessage(response)
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    callback(false)
                }
            }

            override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                Log.e("OrderDetailViewModel", "API call failed: ${t.message}")
                callback(false)
            }
        })
    }


    private fun parseErrorMessage(response: Response<*>): String {
        return try {
            val errorBody = response.errorBody()?.string() ?: return "알 수 없는 오류가 발생했습니다."
            val errorJson = JSONObject(errorBody)
            errorJson.getString("message") // 서버에서 보낸 메시지
        } catch (e: Exception) {
            "오류를 처리할 수 없습니다."
        }
    }
}