package com.example.cosmetictogether.data.api

import com.example.cosmetictogether.data.model.OrderDetailResponse
import com.example.cosmetictogether.data.model.OrderFormResponse
import com.example.cosmetictogether.data.model.OrderListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface OrderRetrofitInterface {

    @GET("/api/v1/order/{formId}")
    fun getOrderDetail(
        @Header("Authorization") token: String,
        @Path("formId") formId: Long
    ): Call<OrderDetailResponse>

    @GET("/api/v1/order/my-order")
    fun getOrderForm(@Header("Authorization") token: String): Call<List<OrderFormResponse>>

    @GET("/api/v1/order/my-form/{formId}")
    fun getOrderList(@Path("formId") formId: Long): Call<OrderListResponse>
}