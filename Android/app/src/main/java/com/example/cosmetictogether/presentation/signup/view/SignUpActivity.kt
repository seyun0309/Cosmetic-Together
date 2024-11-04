package com.example.cosmetictogether.presentation.signup.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.cosmetictogether.R
import com.example.cosmetictogether.presentation.signup.viewmodel.SignUpViewModel

class SignUpActivity : AppCompatActivity() {

    private val viewModel: SignUpViewModel by viewModels()

    private lateinit var nameText: EditText
    private lateinit var nickNameText: EditText
    private lateinit var passwordText: EditText
    private lateinit var confirmPasswordText: EditText
    private lateinit var phoneText: EditText
    private lateinit var addressText: EditText
    private lateinit var detailAddressText: EditText
    private lateinit var passwordCheckMessage: TextView
    private lateinit var signUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        nameText = findViewById(R.id.NameText)
        nickNameText = findViewById(R.id.NickNameText)
        passwordText = findViewById(R.id.SignUpPasswordText)
        confirmPasswordText = findViewById(R.id.SignupPasswordText2)
        phoneText = findViewById(R.id.SignupPhoneText)
        addressText = findViewById(R.id.SignUpAddressText)
        detailAddressText = findViewById(R.id.SignUpHomeText)
        passwordCheckMessage = findViewById(R.id.SignupPasswordMessage2)
        signUpButton = findViewById(R.id.SignUpButton)

        findViewById<Button>(R.id.PasswordCheckBtn).setOnClickListener { checkPassword() }

        signUpButton.setOnClickListener { signUp() }

        viewModel.signUpResult.observe(this, Observer { result ->
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
            if (result == "회원가입 성공!") {
                finish() // Finish the activity on success
            }
        })
    }

    private fun checkPassword() {
        val password = passwordText.text.toString()
        val confirmPassword = confirmPasswordText.text.toString()

        passwordCheckMessage.text = if (password == confirmPassword) {
            "비밀번호가 일치합니다."
        } else {
            "비밀번호가 일치하지 않습니다."
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADDRESS_SEARCH_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.let {
                val address = it.getStringExtra("address")
                val postalCode = it.getStringExtra("postalCode")
                addressText.setText(postalCode)
                detailAddressText.setText(address)
            }
        }
    }

    private fun signUp() {
        val userName = nameText.text.toString()
        val nickname = nickNameText.text.toString()
        val email = nickNameText.text.toString() // Assume email is taken from nickNameText for this example
        val password = passwordText.text.toString()
        val phone = phoneText.text.toString()
        val address = "${addressText.text} ${detailAddressText.text}"

        viewModel.signUp(userName, email, password, phone, nickname, address)
    }

    companion object {
        const val ADDRESS_SEARCH_REQUEST_CODE = 1
    }
}
