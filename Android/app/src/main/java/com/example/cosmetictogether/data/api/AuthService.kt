package com.example.cosmetictogether.data.api

import com.example.cosmetictogether.data.api.AuthRetrofitInterface
import com.example.cosmetictogether.data.model.LoginRequest
import com.example.cosmetictogether.data.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthService {
    private var loginView: LoginView? = null

    fun setLoginView(view: LoginView) {
        this.loginView = view
    }

    fun login(email: String, password: String) {
        val request = LoginRequest(email, password)

        // Assuming RetrofitClient is set up correctly
        val retrofit = Retrofit.Builder()
            .baseUrl("https://yourapi.com") // replace with your base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(AuthRetrofitInterface::class.java)

        apiService.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.accessToken
                    if (token != null) {
                        loginView?.onSignInSuccess(token)
                    } else {
                        loginView?.onSignInFailure()
                    }
                } else {
                    loginView?.onSignInFailure()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loginView?.onSignInFailure()
            }
        })
    }
}
