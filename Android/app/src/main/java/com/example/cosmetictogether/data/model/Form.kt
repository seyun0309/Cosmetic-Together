package com.example.cosmetictogether.data.model

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class FormSummaryResponse(
    @SerializedName("formId") val id: Long,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("title") val title: String,
    @SerializedName("organizerName") val organizerName: String,
    @SerializedName("organizer_url") val organizer_url: String,
    @SerializedName("formStatus") val formStatus: String
)

data class CreateFormRequest(
    @SerializedName("title") val title: String,
    @SerializedName("form_description") val formDescription: String,
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String,
    @SerializedName("productName") val productName: List<String>,
    @SerializedName("price") val price: List<Int>,
    @SerializedName("stock") val stock: List<Int>,
    @SerializedName("maxPurchaseLimit") val maxPurchaseLimit: List<Int>,
    @SerializedName("deliveryOption") val deliveryOption: List<String>,
    @SerializedName("deliveryCost") val deliveryCost: List<Int>,
    @SerializedName("deliveryInstructions") val deliveryInstructions: String
)

data class CreateFormResponse(
    @SerializedName("formId") val formId: Long,
    @SerializedName("productId") val productId: List<Long>
)

data class DetailFormResponse(
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("organizerName") val organizerName: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("address") val address: String,
    @SerializedName("email") val email: String,
    @SerializedName("organizer_profileUrl") val organizerProfileUrl: String,
    @SerializedName("title") val title: String,
    @SerializedName("form_description") val formDescription: String,
    @SerializedName("salesPeriod") val salesPeriod: String,
    @SerializedName("favoriteCount") val favoriteCount: Int,
    @SerializedName("products") val products: List<ResponseProduct>,
    @SerializedName("deliveries") val deliveries: List<Delivery>
)

data class Product(
    val imageUri: Uri?,
    val productName: String,
    val price: Int,
    val stock: Int,
    val maxPurchaseLimit: Int
)


data class ResponseProduct(
    val productId: Long,
    val product_url: String,
    val productName: String,
    val price: Int,
    val stock: Int,
    val maxPurchaseLimit: Int
)

data class Delivery(
    val deliveryOption: String,
    val deliveryCost: Int
)
