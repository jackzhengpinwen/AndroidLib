package com.zpw.net

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.zpw.net.core.ResponseObserver
import com.zpw.net.test.TestViewModel
import com.zpw.net.test.UserResponse

class NetMainActivity : AppCompatActivity() {
    private val TAG = "NetMainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_net_main)
        val testViewModel = ViewModelProvider(this).get(TestViewModel::class.java)
        testViewModel.addUser(1, "zpw")
        testViewModel.addUserDbLiveData.observe(this, object : ResponseObserver<UserResponse>() {
            override fun onSuccess(data: UserResponse?) {
                Log.d(TAG, "onSuccess: $data")
            }
        })
    }
}