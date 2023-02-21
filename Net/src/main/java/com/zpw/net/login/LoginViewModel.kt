package com.zpw.net.login

import androidx.lifecycle.viewModelScope
import com.zpw.net.core.BaseViewModel
import com.zpw.net.core.ResponseLiveData
import com.zpw.net.core.ResponseMutableLiveData
import kotlinx.coroutines.launch

class LoginViewModel : BaseViewModel<LoginRepository>() {
    override fun createRepository(): LoginRepository {
        return repository as LoginRepository
    }
    // 提供给Model层设置数据
    private val _loginLiveData: ResponseMutableLiveData<LoginResponse> = ResponseMutableLiveData()

    // 提供给View层观察数据
    val loginLiveData: ResponseLiveData<LoginResponse> = _loginLiveData

    /**
     * Login
     *  @param  username 用户名
     *  @param  password 密码
     *  @param  client_type 客户端类型
     */
    fun login(username: String, password: String, client_type: String) {
        viewModelScope.launch {
            repository.login(username, password, client_type, _loginLiveData)
        }
    }
}