package com.example.cosmetictogether.data.model

import com.google.gson.annotations.SerializedName

data class OrderFormResponse(
    @SerializedName("formId") val formId: Long,
    @SerializedName("orderId") val orderId: Long,
    @SerializedName("orderStatus") val orderStatus: String,
    @SerializedName("orderDate") val orderDate: String,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("title") val title: String,
    @SerializedName("totalPrice") val totalPrice: String
)

data class OrderDetailResponse(
    @SerializedName("orderDate") val orderDate: String,
    @SerializedName("productPrice") val productPrice: String,
    @SerializedName("shippingFee") val shippingFee: String,
    @SerializedName("totalPayment") val totalPayment: String,
    @SerializedName("recipientName") val recipientName: String,
    @SerializedName("recipientPhone") val recipientPhone: String,
    @SerializedName("recipientAddress") val recipientAddress: String,
    @SerializedName("orderProducts") val orderProducts: List<OrderProductResponse>,
    @SerializedName("deliveryOption") val deliveryOption: String,
    @SerializedName("deliveryCost") val deliveryCost: String,
    @SerializedName("orderStatus") val orderStatus: String,
    @SerializedName("organizerName") val organizerName: String,
    @SerializedName("bankName") val bankName: String,
    @SerializedName("accountNumber") val accountNumber: String
)

data class OrderProductResponse(
    @SerializedName("productId") val productId: Int,
    @SerializedName("productName") val productName: String,
    @SerializedName("price") val price: String,
    @SerializedName("product_url") val productUrl: String,
    @SerializedName("quantity") val quantity: String
)

data class OrderListResponse(
    @SerializedName("totalOrders") val totalOrders: String,
    @SerializedName("totalSales") val totalSales: String,
    @SerializedName("orders") val orders: List<Orders>,
)

data class Orders(
    @SerializedName("formId") val formId: Long,
    @SerializedName("orderId") val orderId: Long,
    @SerializedName("orderDate") val orderDate: String,
    @SerializedName("buyerName") val buyerName: String,
    @SerializedName("totalPrice") val totalPrice: String,
)

data class UpdateOrderStatusRequest(
    @SerializedName("orderStatus") val orderStatus: String
)