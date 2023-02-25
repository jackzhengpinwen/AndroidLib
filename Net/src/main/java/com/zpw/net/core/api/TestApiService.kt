package com.zpw.net.core.api

import com.zpw.net.core.BaseResponse
import com.zpw.net.stock.GZLLResponse
import com.zpw.net.test.UserRequest
import com.zpw.net.test.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TestApiService {
    @POST("/user/add")
    suspend fun addUser(
        @Body userRequest: UserRequest
    ): BaseResponse<UserResponse>
}