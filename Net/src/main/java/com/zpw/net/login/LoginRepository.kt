package com.zpw.net.login

import com.zpw.net.core.BaseRepository
import com.zpw.net.core.ResponseMutableLiveData
import com.zpw.net.core.RetrofitManager

class LoginRepository : BaseRepository() {
    /**
     * Login
     *  @param  account 账号
     *  @param  password 密码
     *  @param  client_type 客户端类型
     */
    suspend fun login(account: String, password: String, client_type: String, responseLiveData: ResponseMutableLiveData<LoginResponse>, showLoading: Boolean = true) {
        executeRequest({
            RetrofitManager.apiService.login(LoginRequest(account, password, client_type))
        }, responseLiveData, showLoading)
    }
}