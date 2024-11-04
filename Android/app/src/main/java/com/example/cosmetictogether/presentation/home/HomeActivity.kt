package com.example.cosmetictogether.presentation.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cosmetictogether.R
import com.example.cosmetictogether.presentation.login.LoginActivity

class HomeActivity : AppCompatActivity() {
    private lateinit var previousButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        previousButton = findViewById(R.id.previousButton) // 버튼 ID와 연결

        previousButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}