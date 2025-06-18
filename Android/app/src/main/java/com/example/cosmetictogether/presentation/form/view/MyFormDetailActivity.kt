package com.example.cosmetictogether.presentation.form.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.cosmetictogether.R
import com.example.cosmetictogether.data.model.ResponseDelivery
import com.example.cosmetictogether.data.model.ResponseProduct
import com.example.cosmetictogether.databinding.ActivityFormDetailBinding
import com.example.cosmetictogether.databinding.ItemFormSelectBinding
import com.example.cosmetictogether.presentation.form.viewmodel.FormDetailViewModel
import com.example.cosmetictogether.presentation.post.view.PostEditActivity

class MyFormDetailActivity: AppCompatActivity() {
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


        binding.backBtn.setOnClickListener{
            val intent = Intent(this, MyFormActivity::class.java)
            startActivity(intent)
        }

        viewModel.orderResponse.observe(this) { response ->
            if (response != null) {
                Toast.makeText(this, "주문 성공", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, FormActivity::class.java)
                intent.putExtra("SHOW_DIALOG", true)
                intent.putExtra("orderId", response.orderId)
                startActivity(intent)
                finish()
            }
        }

        // 점 세개 클릭
        binding.moreOptions.setOnClickListener { view ->
            val popupMenu = PopupMenu(this, view)
            popupMenu.menuInflater.inflate(R.menu.item_menu_options, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        val intent = Intent(this, PostEditActivity::class.java).apply {
                            putExtra("formId", formId)
                        }
                        startActivityForResult(intent, REQUEST_EDIT_POST)
                        true
                    }

                    R.id.action_delete -> {
                        showDeleteDialog(formId)
                        true
                    }

                    else -> false
                }
            }

            popupMenu.show()
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
    }

    private fun showDeleteDialog(formId: Long) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_delete_post, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.yesButton).setOnClickListener {
            viewModel.deleteForm(getToken(), formId)
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.noButton).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun getToken(): String {
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        return sharedPreferences.getString("access_token", null) ?: ""
    }

    companion object {
        private const val REQUEST_EDIT_POST = 1001
    }
}