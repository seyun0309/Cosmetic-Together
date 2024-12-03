package com.example.cosmetictogether.presentation.order.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cosmetictogether.R
import com.example.cosmetictogether.databinding.ActivityOrderDetailBinding
import com.example.cosmetictogether.presentation.form.view.FormActivity
import com.example.cosmetictogether.presentation.mypage.view.MyPageActivity
import com.example.cosmetictogether.presentation.order.adapter.ProductAdapter
import com.example.cosmetictogether.presentation.order.viewmodel.OrderDetailViewModel

class OrderDetailActivity : AppCompatActivity(){
    private lateinit var binding: ActivityOrderDetailBinding
    private val viewModel: OrderDetailViewModel by viewModels()
    private lateinit var productAdapter: ProductAdapter

    private fun getToken(): String {
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("access_token", null) ?: ""
        return "Bearer " + token
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // DataBinding 설정
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        // ViewModel에서 데이터를 관찰
        viewModel.orderDetail.observe(this) { order ->
            binding.order = order // Order 객체를 연결
        }

        // RecyclerView 설정
        productAdapter = ProductAdapter()
        binding.rvProductList.apply {
            layoutManager = LinearLayoutManager(this@OrderDetailActivity)
            adapter = productAdapter
        }

        val orderId = intent.getLongExtra("orderId", -1)
        val token = getToken()

        if (orderId != -1L && token.isNotEmpty()) {
            viewModel.getOrderDetail(this, token, orderId)
        }

        // Observe product list
        viewModel.orderProducts.observe(this, Observer {
            productAdapter.submitList(it)
        })

        binding.backBtn.setOnClickListener {
            val intent = Intent(this, OrderActivity::class.java)
            startActivity(intent)
        }

        // BottomNavigationView의 항목 클릭 이벤트 설정
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_post -> {
                    // PostActivity로 이동할 경우 구현
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
                R.id.action_alarm -> {
                    // AlarmActivity로 이동할 경우 구현
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
}