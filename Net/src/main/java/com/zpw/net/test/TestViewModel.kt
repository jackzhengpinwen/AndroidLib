package com.zpw.net.test

import androidx.lifecycle.viewModelScope
import com.zpw.base.db.GZLL
import com.zpw.net.core.BaseViewModel
import com.zpw.net.core.ResponseLiveData
import com.zpw.net.core.ResponseMutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TestViewModel : BaseViewModel<TestRepository>() {
    // 提供给Model层设置数据
    private val _addUserDbLiveData: ResponseMutableLiveData<UserResponse> = ResponseMutableLiveData()

    // 提供给View层观察数据
    val addUserDbLiveData: ResponseLiveData<UserResponse> = _addUserDbLiveData

    /**
     * 保存数据库国债
     */
    fun addUser(userId: Long, userName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addUser(userId, userName, _addUserDbLiveData)
        }
    }
}