package com.example.cosmetictogether.data.api

import android.util.Log
import com.example.cosmetictogether.data.model.AuthResponse
import com.example.cosmetictogether.data.model.SignInRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthService {
    private var retrofit = RetrofitClient.getRetrofit()
    private lateinit var LoginView: LoginView

    fun setLoginView(LoginView: LoginView) {
        this.LoginView = LoginView
    }

    fun login(email: String, password: String) {
        val authService = retrofit.create(AuthRetrofitInterface::class.java)
        val signInRequest = SignInRequest(email, password) // Create a SignInRequest object
        authService.login(signInRequest).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val access = response.body()!!.access
                    LoginView.onSignInSuccess(access)
                } else {
                    Log.d("API/LOGIN/FAILURE", "Response failed: ${response.code()} - ${response.message()}")
                    LoginView.onSignInFailure()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Log.d("API/LOGIN/FAILURE", "Request failed: ${t.message}")
                LoginView.onSignInFailure()
            }
        })
    }
}