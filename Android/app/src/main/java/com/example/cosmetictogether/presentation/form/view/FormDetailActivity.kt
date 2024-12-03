package com.example.cosmetictogether.presentation.form.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.cosmetictogether.R
import com.example.cosmetictogether.data.model.CreateOrderRequest
import com.example.cosmetictogether.data.model.ResponseDelivery
import com.example.cosmetictogether.data.model.ResponseProduct
import com.example.cosmetictogether.databinding.ActivityFormDetailBinding
import com.example.cosmetictogether.databinding.ItemFormSelectBinding
import com.example.cosmetictogether.presentation.form.viewmodel.FormDetailViewModel

class FormDetailActivity: AppCompatActivity() {
    private lateinit var binding: ActivityFormDetailBinding
    private val viewModel: FormDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 데이터 바인딩을 사용하여 레이아웃을 연결
        binding = DataBindingUtil.setContentView(this, R.layout.activity_form_detail)

        // ViewModel을 바인딩 객체에 설정
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this  // LiveData를 자동으로 갱신하기 위한 설정

        // formId를 전달받아 데이터를 조회
        val formId = intent.getLongExtra("formId", -1L) // 전달된 formId 확인
        if (formId != -1L) {
            val token = "Bearer " + getToken()
            viewModel.getFormDetail(formId, token) // formId로 폼 세부 정보 조회
        }

        // formItem을 관찰하여 값이 변경되면 UI를 갱신
        viewModel.formItem.observe(this, Observer { form ->
            if (form != null) {
                // 상품 항목을 동적으로 추가
                binding.form = form
                viewModel.initializeProducts(form.products.size)
                addProductItems(form.products)
                // 배송 항목을 동적으로 추가
                addDeliveryItems(form.deliveries)
            } else {
                Log.e("FormDetail", "Form data is null!")
            }
        })

        viewModel.finalTotalPrice.observe(this, Observer { price ->
            binding.totalAmountTextView.text = "총 결제 금액: ${price}원"
        })


        binding.backBtn.setOnClickListener{
            val intent = Intent(this, FormActivity::class.java)
            startActivity(intent)
        }

        binding.purchaseButton.setOnClickListener {
            val recipientName = findViewById<EditText>(R.id.shippingName).text.toString()
            val recipientPhone = findViewById<EditText>(R.id.shippingContact).text.toString()
            val recipientAddress = findViewById<EditText>(R.id.shippingAddress).text.toString()
            val totalPrice = viewModel.finalTotalPrice.value ?: 0

            // ViewModel에서 수량 데이터를 가져옴
            val (productsId, orderQuantity) = viewModel.getSelectedProducts()
            val selectedDeliveryId = viewModel.selectedDeliveryId.value

            if (selectedDeliveryId == null) {
                Toast.makeText(this, "배송 방법을 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (productsId.isEmpty() || orderQuantity.isEmpty()) {
                Toast.makeText(this, "수량이 입력된 상품이 없습니다.", Toast.LENGTH_SHORT).show()
            }

            val createOrderRequest = CreateOrderRequest(
                recipientName = recipientName,
                recipientPhone = recipientPhone,
                recipientAddress = recipientAddress,
                productsId = productsId, // 실제 데이터를 넣어야 함
                orderQuantity = orderQuantity, // 실제 데이터를 넣어야 함
                totalPrice = totalPrice,
                deliveryId = selectedDeliveryId
            )

            val token = "Bearer " + getToken()
            viewModel.createOrder(this, formId, token, createOrderRequest)
        }

        viewModel.orderResponse.observe(this) { response ->
            if (response != null) {
                Toast.makeText(this, "주문 성공: ${response.message}", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, FormActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        val followButton = binding.root.findViewById<Button>(R.id.followButton)

        viewModel.isFollowing.observe(this) { isFollowing ->
            val followButton = findViewById<Button>(R.id.followButton)
            if (isFollowing) {
                followButton.text = "언팔로우"
                followButton.setBackgroundColor(Color.parseColor("#CEDDFE"))
            } else {
                followButton.text = "팔로우"
                followButton.setBackgroundColor(Color.parseColor("#FFFFFF"))
            }
        }

        // 버튼 클릭 리스너 설정
        followButton.setOnClickListener {
            val token = "Bearer " + getToken()
            val organizerId = viewModel.organizerId.value ?: return@setOnClickListener

            if (viewModel.isFollowing.value == true) {
                viewModel.followUser(this, token, organizerId)
            } else {
                viewModel.followUser(this, token, organizerId)
            }
        }

        viewModel.organizerId.observe(this) { organizerId ->
            // 필요하면 UI 업데이트 로직 추가
            Log.d("팔로우 버튼 관찰", "Organizer ID: $organizerId")
        }
    }

    // 상품 항목을 동적으로 추가하는 함수
    private fun addProductItems(products: List<ResponseProduct>) {
        val layoutContainer = findViewById<LinearLayout>(R.id.layoutProductContainer)
        layoutContainer.removeAllViews()

        products.forEachIndexed { index, product ->
            val productView = layoutInflater.inflate(R.layout.item_form_select, null, false)
            val productBinding = ItemFormSelectBinding.bind(productView)
            productBinding.viewmodel = viewModel
            productBinding.product = product

            // 개별 상품의 수량 LiveData 관찰
            viewModel.getQuantityLiveData(product.productId).observe(this) { quantity ->
                productBinding.quantityTextView.text = (quantity ?: 0).toString()
            }

            // 증가/감소 버튼 클릭 시 개별 상품 가격 변동 없이 전체 결제 금액만 변동
            productBinding.incrementButton.setOnClickListener {
                viewModel.increaseQuantity(product.productId, product.maxPurchaseLimit, product.price)
            }
            productBinding.decrementButton.setOnClickListener {
                viewModel.decreaseQuantity(product.productId, product.price)
            }

            layoutContainer.addView(productView)
        }
    }

    // 배송 항목을 동적으로 추가하는 함수
    private fun addDeliveryItems(deliveries: List<ResponseDelivery>) {
        val layoutContainer = findViewById<LinearLayout>(R.id.layoutDeliveryContainer)
        layoutContainer.removeAllViews()

        val radioGroup = RadioGroup(this).apply { orientation = RadioGroup.VERTICAL }

        deliveries.forEach { delivery ->
            val radioButton = RadioButton(this).apply {
                text = "${delivery.deliveryOption} (${delivery.deliveryCost}원)"
                tag = delivery.deliveryId // deliveryId를 태그로 설정
                setOnClickListener {
                    viewModel.setDeliveryPrice(delivery.deliveryCost)
                    viewModel.setSelectedDeliveryId(delivery.deliveryId) // 선택한 배송 ID를 ViewModel에 저장
                }
            }
            radioGroup.addView(radioButton)
        }

        layoutContainer.addView(radioGroup)
    }

    private fun setupListeners() {
        val orderEditText1 = binding.root.findViewById<EditText>(R.id.shippingName)
        val orderEditText2 = binding.root.findViewById<EditText>(R.id.shippingContact)
        val orderEditText3 = binding.root.findViewById<EditText>(R.id.shippingAddress)
        val deliveryRadioGroup = binding.root.findViewById<RadioGroup>(R.id.deliveryRadioGroup)

        val editTexts = listOf(orderEditText1, orderEditText2, orderEditText3)

        // EditText 변경 감지
        editTexts.forEach { editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    // 모든 EditText가 채워졌는지 확인
                    val allFilled = editTexts.all { it.text.toString().isNotEmpty() }
                    viewModel.updateOrderInputsValid(allFilled)
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // 사용하지 않는 메서드는 빈 구현으로 유지
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // 사용하지 않는 메서드는 빈 구현으로 유지
                }
            })
        }

        // RadioButton 선택 감지
        deliveryRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedIndex = deliveryRadioGroup.indexOfChild(findViewById(checkedId))
            val isSelected = checkedId != -1
            viewModel.updateDeliverySelection(isSelected)
        }

        // 상품 수량 변경 감지
        viewModel.quantityList.observe(this) { quantities ->
            val isValid = quantities.any { it > 0 }
            viewModel.updateProductQuantityValidity(isValid)
        }

        // 구매하기 버튼 활성화 상태 관찰
        viewModel.isPurchaseButtonEnabled.observe(this) { isEnabled ->
            binding.purchaseButton.isEnabled = isEnabled
        }
    }

    private fun getToken(): String {
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        return sharedPreferences.getString("access_token", null) ?: ""
    }
}