package com.zpw.net

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.zpw.net.core.ResponseObserver
import com.zpw.net.login.LoginResponse
import com.zpw.net.login.LoginViewModel

class NetMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userIdView = findViewById<TextView>(R.id.user_id)
        val loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        loginViewModel.login("zhengpinwen@gmail.com", "Remo@123", "px30")
        loginViewModel.loginLiveData.observe(this, object : ResponseObserver<LoginResponse>() {
            override fun onSuccess(data: LoginResponse?) {
                userIdView.text = data?.user_id
            }

        })
    }
}