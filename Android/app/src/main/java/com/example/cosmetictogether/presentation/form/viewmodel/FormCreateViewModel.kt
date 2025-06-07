package com.example.cosmetictogether.presentation.form.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cosmetictogether.data.api.FormRetrofitInterface
import com.example.cosmetictogether.data.api.RetrofitClient
import com.example.cosmetictogether.data.model.CreateFormRequest
import com.example.cosmetictogether.data.model.CreateFormResponse
import com.example.cosmetictogether.data.model.Delivery
import com.example.cosmetictogether.data.model.Product
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FormCreateViewModel: ViewModel() {
    // 상품 데이터 리스트
    private val _productList = MutableLiveData<MutableList<Product>>()
    val productList: LiveData<MutableList<Product>> get() = _productList

    // 배송 방법 리스트
    private val _deliveryList = MutableLiveData<MutableList<Delivery>>()
    val deliveryList: LiveData<MutableList<Delivery>> get() = _deliveryList


    // 폼 생성 응답 데이터 저장 LiveData
    private val _createFormResponse = MutableLiveData<CreateFormResponse?>()
    val createFormResponse: LiveData<CreateFormResponse?> get() = _createFormResponse

    private val formApi: FormRetrofitInterface =
        RetrofitClient.getInstance().create(FormRetrofitInterface::class.java)


    init {
        _productList.value = mutableListOf()
        _deliveryList.value = mutableListOf()
    }

    fun addProduct(product: Product) {
        val updatedList = _productList.value?.toMutableList() ?: mutableListOf()
        updatedList.add(product)
        _productList.value = updatedList // List 변경 후 LiveData 업데이트
    }

    fun addDelivery(deliveryMethod: Delivery) {
        val updatedList = _deliveryList.value?.toMutableList() ?: mutableListOf()
        updatedList.add(deliveryMethod)
        _deliveryList.value = updatedList // List 변경 후 LiveData 업데이트
    }

    fun updateProduct(updatedProduct: Product) {
        val updatedList = _productList.value?.toMutableList() ?: mutableListOf()
        val index = updatedList.indexOfFirst { it.productName == updatedProduct.productName }
        if (index != -1) {
            updatedList[index] = updatedProduct
            _productList.value = updatedList
        }
    }

    fun removeProduct(product: Product) {
        val updatedList = _productList.value?.toMutableList() ?: mutableListOf()
        updatedList.remove(product) // 아이템 제거
        _productList.value = updatedList // List 변경 후 LiveData 업데이트
    }

    fun removeDelivery(delivery: Delivery) {
        val updatedList = _deliveryList.value?.toMutableList() ?: mutableListOf()
        updatedList.remove(delivery) // 아이템 제거
        _deliveryList.value = updatedList // List 변경 후 LiveData 업데이트
    }

    // 폼 작성 요청 메서드
    fun submitForm(token: String, thumbnail: MultipartBody.Part, request: CreateFormRequest, images: List<MultipartBody.Part>) {
        Log.d("deliveryCost.size", request.startDate)
        formApi.createForm(token, thumbnail, request, images).enqueue(object : Callback<CreateFormResponse> {
            override fun onResponse(call: Call<CreateFormResponse>, response: Response<CreateFormResponse>) {
                if (response.isSuccessful) {
                // 서버에서 보낸 응답을 처리
                response.body()?.let { data ->
                    // 응답이 성공적인 경우, 필요한 처리
                    _createFormResponse.value = data
                    // 예시: 응답 값에서 메시지를 사용
                    val responseMessage = "폼 생성 완료"
                }
            } else {
                // 응답이 실패한 경우 (예: 4xx, 5xx 상태 코드)
                Log.e("FormSubmit", "폼 생성 실패: ${response.code()} - ${response.message()}")
            }
            }

            override fun onFailure(call: Call<CreateFormResponse>, t: Throwable) {
                // 네트워크 실패 시 처리
                Log.e("FormSubmit", "네트워크 오류: ${t.message}")
            }
        })
    }
}