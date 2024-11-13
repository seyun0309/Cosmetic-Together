package com.example.cosmetictogether.presentation.form.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.cosmetictogether.R
import com.example.cosmetictogether.data.model.Delivery
import com.example.cosmetictogether.data.model.ResponseProduct
import com.example.cosmetictogether.databinding.ActivityFormDetailBinding
import com.example.cosmetictogether.databinding.ItemFormDeliveryBinding
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
            viewModel.getFormDetail(formId) // formId로 폼 세부 정보 조회
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
    private fun addDeliveryItems(deliveries: List<Delivery>) {
        val layoutContainer = findViewById<LinearLayout>(R.id.layoutDeliveryContainer)
        layoutContainer.removeAllViews()

        val radioGroup = RadioGroup(this).apply { orientation = RadioGroup.VERTICAL }

        deliveries.forEach { delivery ->
            val radioButton = RadioButton(this).apply {
                text = "${delivery.deliveryOption} (${delivery.deliveryCost}원)"
                setOnClickListener {
                    viewModel.setDeliveryPrice(delivery.deliveryCost)
                }
            }
            radioGroup.addView(radioButton)
        }

        layoutContainer.addView(radioGroup)
    }
}