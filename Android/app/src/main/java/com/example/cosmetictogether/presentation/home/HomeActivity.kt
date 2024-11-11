package com.example.cosmetictogether.presentation.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.cosmetictogether.R
import com.example.cosmetictogether.presentation.form.view.FormActivity
import com.example.cosmetictogether.presentation.form.view.FormCreateActivity
import com.example.cosmetictogether.presentation.mypage.view.MyPageActivity

class HomeActivity : AppCompatActivity() {
    private lateinit var previousButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        previousButton = findViewById(R.id.previousButton) // 버튼 ID와 연결

        previousButton.setOnClickListener {
            startActivity(Intent(this, FormActivity::class.java))
        }
    }
}