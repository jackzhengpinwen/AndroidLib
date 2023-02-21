package com.zpw.net.core.api

import com.zpw.net.core.BaseResponse
import com.zpw.net.login.LoginRequest
import com.zpw.net.login.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("ums/v1/users/login")
    suspend fun login(
        @Body request: LoginRequest
    ): BaseResponse<LoginResponse>
}