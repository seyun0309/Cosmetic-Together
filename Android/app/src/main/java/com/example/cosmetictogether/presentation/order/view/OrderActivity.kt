package com.example.cosmetictogether.presentation.order.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cosmetictogether.R
import com.example.cosmetictogether.databinding.ActivityOrderBinding
import com.example.cosmetictogether.presentation.form.view.FormActivity
import com.example.cosmetictogether.presentation.form.view.FormDetailActivity
import com.example.cosmetictogether.presentation.mypage.view.MyPageActivity
import com.example.cosmetictogether.presentation.order.adapter.OrderAdapter
import com.example.cosmetictogether.presentation.order.viewmodel.OrderViewModel
import com.example.cosmetictogether.presentation.post.view.PostActivity

class OrderActivity : AppCompatActivity(){
    private lateinit var binding: ActivityOrderBinding
    private val viewModel: OrderViewModel by viewModels()

    private lateinit var orderAdapter: OrderAdapter // 어댑터 선언

    private fun getToken(): String {
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("access_token", null) ?: ""
        return "Bearer " + token
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // RecyclerView 및 Adapter 초기화
        orderAdapter = OrderAdapter(
            emptyList(),
            onFormDetailClick = { formId -> navigateToFormDetail(formId) }, // 기존 btnFormDetail 로직
            onOrderDetailClick = { orderId -> navigateToOrderDetail(orderId) } // 새로운 btnOrderDetail 로직
        )

        binding.orderRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.orderRecyclerView.adapter = orderAdapter

        // ViewModel 관찰
        viewModel.orderForms.observe(this) { orders ->
            orderAdapter.updateData(orders) // RecyclerView 데이터 업데이트
        }

        val token = getToken()

        viewModel.loadOrderForm(token)

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> {
                    val intent = Intent(this, PostActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.action_form -> {
                    val intent = Intent(this, FormActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.action_home -> {
                    // HomeActivity로 이동할 경우 구현
                    true
                }
                R.id.action_mypage -> {
                    // MyPageActivity로 이동
                    val intent = Intent(this, MyPageActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun navigateToFormDetail(formId: Long) {
        val intent = Intent(this, FormDetailActivity::class.java)
        intent.putExtra("formId", formId) // 폼 ID 전달
        startActivity(intent)
    }

    private fun navigateToOrderDetail(orderId: Long) {
        val intent = Intent(this, OrderDetailActivity::class.java)
        intent.putExtra("orderId", orderId)
        startActivity(intent)
    }
}