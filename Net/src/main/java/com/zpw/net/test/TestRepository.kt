package com.zpw.net.test

import com.zpw.base.db.AppDatabase
import com.zpw.base.db.GZLL
import com.zpw.net.core.BaseRepository
import com.zpw.net.core.ResponseLiveData
import com.zpw.net.core.ResponseMutableLiveData
import com.zpw.net.core.RetrofitManager
import com.zpw.net.stock.GZLLResponse

class TestRepository : BaseRepository() {
    suspend fun addUser(userId: Long, userName: String, responseLiveData: ResponseMutableLiveData<UserResponse>, showLoading: Boolean = true) {
        executeNetRequest({
            RetrofitManager.testService.addUser(UserRequest(userId, userName))
        }, responseLiveData, showLoading)
    }
}