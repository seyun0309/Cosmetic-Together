package com.example.cosmetictogether.presentation.form.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cosmetictogether.R
import com.example.cosmetictogether.databinding.ActivityMyformBinding
import com.example.cosmetictogether.presentation.form.adapter.MyFormAdapter
import com.example.cosmetictogether.presentation.form.viewmodel.MyFormViewModel
import com.example.cosmetictogether.presentation.mypage.view.MyPageActivity
import com.example.cosmetictogether.presentation.order.view.MyFormOrderActivity

class MyFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyformBinding
    private val viewModel: MyFormViewModel by viewModels()

    private lateinit var myFormAdapter: MyFormAdapter// 어댑터 선언

    private fun getToken(): String {
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("access_token", null) ?: ""
        return "Bearer $token"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyformBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        myFormAdapter = MyFormAdapter(
            emptyList(),
            onFormDetailClick = { formId -> navigateToFormDetail(formId) }, // 기존 btnFormDetail 로직
            onOrderListClick = { formId -> navigateToOrderList(formId) } // 새로운 btnOrderDetail 로직
        )

        binding.formRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.formRecyclerView.adapter = myFormAdapter

        // ViewModel 관찰
        viewModel.myForms.observe(this) { forms ->
            myFormAdapter.updateData(forms) // RecyclerView 데이터 업데이트
        }

        viewModel.loadFormList(getToken())

        binding.backBtn.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }

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


    private fun navigateToFormDetail(formId: Long) {
        val intent = Intent(this, FormDetailActivity::class.java)
        intent.putExtra("formId", formId) // 폼 ID 전달
        startActivity(intent)
    }

    private fun navigateToOrderList(formId: Long) {
        val intent = Intent(this, MyFormOrderActivity::class.java)
        intent.putExtra("formId", formId)
        startActivity(intent)
    }
}