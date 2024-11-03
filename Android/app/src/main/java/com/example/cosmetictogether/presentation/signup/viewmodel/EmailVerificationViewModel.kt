package com.example.cosmetictogether.presentation.signup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cosmetictogether.data.api.AuthRetrofitInterface
import com.example.cosmetictogether.data.api.RetrofitClient
import com.example.cosmetictogether.data.model.SendEmailResponse
import com.example.cosmetictogether.data.model.SendVerifiedResponse
import com.example.cosmetictogether.data.model.EmailRequest
import com.example.cosmetictogether.data.model.VerificationRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmailVerificationViewModel : ViewModel() {

    fun sendVerificationEmail(email: String): LiveData<SendEmailResponse?> {
        val responseLiveData = MutableLiveData<SendEmailResponse?>()
        val emailService = RetrofitClient.getRetrofit().create(AuthRetrofitInterface::class.java)

        // Create the EmailRequest object
        val emailRequest = EmailRequest(email)
        val call = emailService.sendEmail(emailRequest)

        call.enqueue(object : Callback<SendEmailResponse> {
            override fun onResponse(call: Call<SendEmailResponse>, response: Response<SendEmailResponse>) {
                if (response.isSuccessful) {
                    responseLiveData.postValue(null)
                } else {
                    responseLiveData.postValue(null)
                }
            }

            override fun onFailure(call: Call<SendEmailResponse>, t: Throwable) {
                responseLiveData.postValue(null)
            }
        })

        return responseLiveData
    }

    fun verifyEmailCode(email: String, authCode: String): LiveData<SendVerifiedResponse?> {
        val responseLiveData = MutableLiveData<SendVerifiedResponse?>()
        val emailService = RetrofitClient.getRetrofit().create(AuthRetrofitInterface::class.java)

        // Create the VerificationRequest object
        val verificationRequest = VerificationRequest(email, authCode)
        val call = emailService.sendVerified(verificationRequest)

        call.enqueue(object : Callback<SendVerifiedResponse> {
            override fun onResponse(call: Call<SendVerifiedResponse>, response: Response<SendVerifiedResponse>) {
                if (response.isSuccessful && response.body()?.Verified == true) {
                    responseLiveData.postValue(response.body())
                } else {
                    responseLiveData.postValue(null)
                }
            }

            override fun onFailure(call: Call<SendVerifiedResponse>, t: Throwable) {
                responseLiveData.postValue(null)
            }
        })

        return responseLiveData
    }
}
