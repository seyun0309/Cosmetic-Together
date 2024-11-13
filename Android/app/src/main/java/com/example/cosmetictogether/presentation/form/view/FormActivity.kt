package com.example.cosmetictogether.presentation.form.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.cosmetictogether.R
import com.example.cosmetictogether.databinding.ActivityFormBinding
import com.example.cosmetictogether.presentation.form.adapter.FormAdapter
import com.example.cosmetictogether.presentation.form.viewmodel.FormViewModel
import com.example.cosmetictogether.presentation.mypage.view.MyPageActivity

class FormActivity : AppCompatActivity() {
    private lateinit var formAdapter: FormAdapter
    private lateinit var binding: ActivityFormBinding
    private val viewModel: FormViewModel by viewModels()

    private fun getToken(): String {
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("access_token", null) ?: ""
        return token
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // ListAdapter 사용하여 어댑터 초기화
        formAdapter = FormAdapter()
        binding.formRecyclerView.adapter = formAdapter

        viewModel.loadFormSummaryData()

        // 폼 작성 버튼 클릭
        binding.createFormBtn.setOnClickListener {
            startActivity(Intent(this, FormCreateActivity::class.java))
        }

        // 데이터 업데이트 시 RecyclerView에 반영
        viewModel.formData.observe(this, Observer { formList ->
            formAdapter.submitList(formList) // ListAdapter에서는 submitList 사용 가능
        })

        //폼 세부 조회
        formAdapter.setOnItemClickListener { formId ->
            // 선택된 formId를 Intent로 전달하여 FormDetailActivity로 이동
            val intent = Intent(this, FormDetailActivity::class.java)
            intent.putExtra("formId", formId)
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
                    // 현재 FormActivity이므로 이동하지 않음
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
