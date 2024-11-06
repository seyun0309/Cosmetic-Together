package com.example.cosmetictogether.presentation.form.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.cosmetictogether.R
import com.example.cosmetictogether.databinding.ActivityFormBinding
import com.example.cosmetictogether.presentation.form.viewmodel.FormViewModel
import com.example.cosmetictogether.presentation.mypage.view.MyPageActivity

class FormActivity: AppCompatActivity() {

    private lateinit var binding: ActivityFormBinding
    private val viewModel: FormViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.loadFormSummaryData()

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