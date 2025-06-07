package com.example.cosmetictogether.presentation.order.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cosmetictogether.R
import com.example.cosmetictogether.databinding.ActivityMyformOrderDetailBinding
import com.example.cosmetictogether.presentation.mypage.view.MyPageActivity
import com.example.cosmetictogether.presentation.order.adapter.ProductAdapter
import com.example.cosmetictogether.presentation.order.viewmodel.MyFormOrderDetailViewModel
import com.example.cosmetictogether.presentation.post.view.PostActivity
import com.example.cosmetictogether.presentation.search.view.SearchActivity

class MyFormOrderDetailActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMyformOrderDetailBinding
    private val viewModel: MyFormOrderDetailViewModel by viewModels()
    private lateinit var productAdapter: ProductAdapter

    private fun getToken(): String {
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("access_token", null) ?: ""
        return "Bearer " + token
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // DataBinding 설정
        binding = DataBindingUtil.setContentView(this, R.layout.activity_myform_order_detail)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        // ViewModel에서 데이터를 관찰
        viewModel.orderDetail.observe(this) { order ->
            binding.order = order // Order 객체를 연결
        }

        // RecyclerView 설정
        productAdapter = ProductAdapter()
        binding.rvProductList.apply {
            layoutManager = LinearLayoutManager(this@MyFormOrderDetailActivity)
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
            val formId = intent.getLongExtra("formId", -1)
            Log.d("ID 확인", "formId: $formId") // 로그 출력 형식 수정
            val intent = Intent(this, MyFormOrderActivity::class.java)
            intent.putExtra("formId", formId)
            startActivity(intent)
        }

        // BottomNavigationView의 항목 클릭 이벤트 설정
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> {
                    startActivity(Intent(this, PostActivity::class.java))
                    true
                }
                R.id.action_form -> {
                    // 현재 FormActivity이므로 이동하지 않음
                    true
                }
                R.id.action_search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                    true
                }
                R.id.action_mypage -> {
                    // MyPageActivity로 이동
                    startActivity(Intent(this, MyPageActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}