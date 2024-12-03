package com.example.cosmetictogether.presentation.form.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cosmetictogether.data.api.FormRetrofitInterface
import com.example.cosmetictogether.data.api.RetrofitClient
import com.example.cosmetictogether.data.model.APIResponse
import com.example.cosmetictogether.data.model.CreateOrderRequest
import com.example.cosmetictogether.data.model.DetailFormResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
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

    private val formApi: FormRetrofitInterface =
        RetrofitClient.getInstance().create(FormRetrofitInterface::class.java)

    private val _orderResponse = MutableLiveData<APIResponse>()
    val orderResponse: LiveData<APIResponse> get() = _orderResponse

    private val _isOrderInputsValid = MutableLiveData(false)
    val isOrderInputsValid: LiveData<Boolean> get() = _isOrderInputsValid

    private val _isDeliverySelected = MutableLiveData(false)
    val isDeliverySelected: LiveData<Boolean> get() = _isDeliverySelected

    private val _isProductQuantityValid = MutableLiveData(false)
    val isProductQuantityValid: LiveData<Boolean> get() = _isProductQuantityValid

    private val _selectedDeliveryOptionIndex = MutableLiveData<Int?>()
    val selectedDeliveryOptionIndex: LiveData<Int?> get() = _selectedDeliveryOptionIndex

    private val _selectedDeliveryId = MutableLiveData<Long?>()
    val selectedDeliveryId: LiveData<Long?> get() = _selectedDeliveryId

    private val _organizerId = MutableLiveData<Long>()
    val organizerId: LiveData<Long> get() = _organizerId

    private val _isFollowing = MutableLiveData<Boolean>()
    val isFollowing: LiveData<Boolean> get() = _isFollowing

    init {
        _quantityMap.value = mutableMapOf()
        _totalPriceMap.value = mutableMapOf()
    }

    // 버튼 활성화 여부를 갱신
    private fun validateButtonState() {
        val allValid = (_isOrderInputsValid.value == true
                && _isDeliverySelected.value == true
                && _isProductQuantityValid.value == true)
        (isPurchaseButtonEnabled as MediatorLiveData).value = allValid
    }

    fun setOrganizerId(id: Long) {
        _organizerId.value = id
    }

    fun updateOrderInputsValid(isValid: Boolean) {
        _isOrderInputsValid.value = isValid
    }

    fun updateDeliverySelection(isSelected: Boolean) {
        _isDeliverySelected.value = isSelected
    }

    fun updateProductQuantityValidity(isValid: Boolean) {
        _isProductQuantityValid.value = isValid
    }

    // 버튼 활성화 여부를 결정하는 LiveData
    val isPurchaseButtonEnabled: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(isOrderInputsValid) { validateButtonState() }
        addSource(isDeliverySelected) { validateButtonState() }
        addSource(isProductQuantityValid) { validateButtonState() }
    }

    // 폼 세부 정보 조회
    fun getFormDetail(formId: Long, token: String) {
        formApi.getFormDetail(formId, token).enqueue(object : Callback<DetailFormResponse> {
            override fun onResponse(call: Call<DetailFormResponse>, response: Response<DetailFormResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        _formItem.value = data
                        // 여기에서 products의 개수로 initializeProducts 호출
                        initializeProducts(data.products.size)
                        setOrganizerId(data.organizerId)
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

    // 라디오 버튼에 배송 ID 연결
    fun setSelectedDeliveryId(deliveryId: Long) {
        _selectedDeliveryId.value = deliveryId
    }

    // 수량에 대한 LiveData 제공 메서드
    fun getQuantityLiveData(productId: Long): LiveData<Int> {
        val liveData = MutableLiveData<Int>()
        _quantityMap.observeForever { map ->
            liveData.value = map[productId] ?: 0
        }
        return liveData
    }

    // 수량 확인
    fun getSelectedProducts(): Pair<List<Long>, List<Int>> {
        val quantityMap = _quantityMap.value ?: return Pair(emptyList(), emptyList())
        val products = formItem.value?.products ?: return Pair(emptyList(), emptyList())

        // 수량이 0보다 큰 상품만 필터링하여 productsId와 orderQuantity 리스트 생성
        val productsId = mutableListOf<Long>()
        val orderQuantity = mutableListOf<Int>()

        products.forEach { product ->
            val quantity = quantityMap[product.productId] ?: 0
            if (quantity > 0) {
                productsId.add(product.productId)
                orderQuantity.add(quantity)
            }
        }

        return Pair(productsId, orderQuantity)
    }

    // 주문
    fun createOrder(context: Context, formId: Long, token: String, orderRequest: CreateOrderRequest) {
        formApi.createOrder(formId, token, orderRequest).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {
                if (response.isSuccessful) {
                    _orderResponse.value = response.body()

                } else {
                    val errorMessage = parseErrorMessage(response)
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                // 네트워크 오류 처리
            }
        })
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

    fun followUser(context: Context, token: String, followingId: Long) {
        formApi.followUser(followingId, token).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {
                if (response.isSuccessful) {
                    _orderResponse.value = response.body()
                    val isCurrentlyFollowing = _isFollowing.value ?: false
                    _isFollowing.value = !isCurrentlyFollowing
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                } else {
                    val errorMessage = parseErrorMessage(response)
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    _isFollowing.value = false
                }
            }

            override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                Toast.makeText(context, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                _isFollowing.value = false
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