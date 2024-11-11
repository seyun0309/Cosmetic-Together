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

data class Product(
    val imageUri: Uri?,
    val name: String,
    val price: Int,
    val stock: Int,
    val purchaseLimit: Int
)

data class Delivery(
    val option: String,
    val cost: Int
)
