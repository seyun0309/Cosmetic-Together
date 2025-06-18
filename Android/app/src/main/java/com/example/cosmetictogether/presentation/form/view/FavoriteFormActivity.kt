package com.example.cosmetictogether.presentation.form.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.cosmetictogether.R
import com.example.cosmetictogether.databinding.ActivityFavoriteFormBinding
import com.example.cosmetictogether.presentation.form.adapter.FormAdapter
import com.example.cosmetictogether.presentation.form.viewmodel.FavoriteFormViewModel

class FavoriteFormActivity : AppCompatActivity() {
    private lateinit var formAdapter : FormAdapter
    private lateinit var binding : ActivityFavoriteFormBinding
    private val viewModel : FavoriteFormViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFavoriteFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // ListAdapter 사용하여 어댑터 초기화
        formAdapter = FormAdapter()
        binding.formRecyclerView.adapter = formAdapter

        viewModel.loadFollowingForm(getToken())

        // 뒤로가기 버튼
        binding.backBtn.setOnClickListener {
            finish()
        }

        //폼 세부 조회
        formAdapter.setOnItemClickListener { formId ->
            val intent = Intent(this, FormDetailActivity::class.java)
            intent.putExtra("formId", formId)
            startActivity(intent)
        }

        // 데이터 업데이트 시 RecyclerView에 반영
        viewModel.formData.observe(this, Observer { formList ->
            formAdapter.submitList(formList) // ListAdapter에서는 submitList 사용 가능
        })

        binding.bottomNavigationView.selectedItemId = R.id.action_form

    }

    private fun getToken(): String {
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("access_token", null) ?: ""
        val authToken = "Bearer $token"
        return authToken
    }
}