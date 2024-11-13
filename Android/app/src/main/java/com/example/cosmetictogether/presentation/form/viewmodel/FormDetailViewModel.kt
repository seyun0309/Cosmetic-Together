package com.example.cosmetictogether.presentation.form.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cosmetictogether.data.api.FormRetrofitInterface
import com.example.cosmetictogether.data.api.RetrofitClient
import com.example.cosmetictogether.data.model.DetailFormResponse
import retrofit2.Call
import retrofit2.Response

class FormDetailViewModel : ViewModel() {
    private val _formItem = MutableLiveData<DetailFormResponse>()
    val formItem: LiveData<DetailFormResponse> get() = _formItem

    // 각 productId별 수량과 가격을 저장하는 MutableLiveData Map
    private val _quantityMap = MutableLiveData<MutableMap<Long, Int>>() // productId를 Long으로 변경
    private val _totalPriceMap = MutableLiveData<MutableMap<Long, Int>>() // productId를 Long으로 변경

    // 각 상품별 수량과 총 가격을 개별 LiveData로 관리
    private val _quantityList = MutableLiveData<MutableList<Int>>()
    val quantityList: LiveData<MutableList<Int>> get() = _quantityList

    private val _totalPriceList = MutableLiveData<MutableList<Int>>()
    val totalPriceList: LiveData<MutableList<Int>> get() = _totalPriceList

    private val _deliveryPrice = MutableLiveData(0)
    val deliveryPrice: LiveData<Int> get() = _deliveryPrice

    private val _finalTotalPrice = MutableLiveData(0)
    val finalTotalPrice: LiveData<Int> get() = _finalTotalPrice

    // Map to store each product's quantity LiveData by index
    private val quantityMap = mutableMapOf<Int, MutableLiveData<Int>>()

    private val formApi: FormRetrofitInterface =
        RetrofitClient.getInstance().create(FormRetrofitInterface::class.java)

    init {
        _quantityMap.value = mutableMapOf()
        _totalPriceMap.value = mutableMapOf()
    }

    // 폼 세부 정보 조회
    fun getFormDetail(formId: Long) {
        formApi.getFormDetail(formId).enqueue(object : retrofit2.Callback<DetailFormResponse> {
            override fun onResponse(call: Call<DetailFormResponse>, response: Response<DetailFormResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        _formItem.value = data
                        // 여기에서 products의 개수로 initializeProducts 호출
                        initializeProducts(data.products.size)
                    }
                }
            }

            override fun onFailure(call: Call<DetailFormResponse>, t: Throwable) {
                Log.e("getFormDetail", "Error: ${t.message}")
            }
        })
    }

    fun initializeProducts(productCount: Int) {
        _quantityList.value = MutableList(productCount) { 0 }
        _totalPriceList.value = MutableList(productCount) { 0 }
        calculateFinalTotalPrice() // 초기 결제 금액 계산
    }

    // 수량 변경 시 최종 결제 금액을 업데이트
    fun increaseQuantity(productId: Long, maxQuantity: Int, price: Int) {
        val quantityMap = _quantityMap.value ?: mutableMapOf()
        val currentQuantity = quantityMap[productId] ?: 0
        if (currentQuantity < maxQuantity) {
            quantityMap[productId] = currentQuantity + 1
            _quantityMap.value = quantityMap
            calculateFinalTotalPrice()  // 변경된 수량을 반영해 총 결제 금액 업데이트
        }
    }

    fun decreaseQuantity(productId: Long, price: Int) {
        val quantityMap = _quantityMap.value ?: mutableMapOf()
        val currentQuantity = quantityMap[productId] ?: 0
        if (currentQuantity > 0) {
            quantityMap[productId] = currentQuantity - 1
            _quantityMap.value = quantityMap
            calculateFinalTotalPrice()  // 변경된 수량을 반영해 총 결제 금액 업데이트
        }
    }

    // 배송 금액 설정 시 호출
    fun setDeliveryPrice(price: Int) {
        _deliveryPrice.value = price
        calculateFinalTotalPrice()  // 배송 금액 설정 시 최종 결제 금액 업데이트
    }

    // 수량에 대한 LiveData 제공 메서드
    fun getQuantityLiveData(productId: Long): LiveData<Int> {
        val liveData = MutableLiveData<Int>()
        _quantityMap.observeForever { map ->
            liveData.value = map[productId] ?: 0
        }
        return liveData
    }

    // 전체 결제 금액 계산
    private fun calculateFinalTotalPrice(price: Int, isIncrease: Boolean) {
        val currentTotal = _finalTotalPrice.value ?: 0
        _finalTotalPrice.value = if (isIncrease) currentTotal + price else currentTotal - price
    }

    private fun calculateProductTotalPrice(): Int {
        val quantityMap = _quantityMap.value ?: return 0
        val products = formItem.value?.products ?: return 0
        return products.sumOf { product ->
            val quantity = quantityMap[product.productId] ?: 0
            product.price * quantity
        }
    }

    private fun calculateFinalTotalPrice() {
        val productTotal = calculateProductTotalPrice()  // 모든 상품의 총 합계를 계산
        val deliveryCost = _deliveryPrice.value ?: 0
        _finalTotalPrice.value = productTotal + deliveryCost  // 배송 금액과 상품 합계를 더함
    }
}