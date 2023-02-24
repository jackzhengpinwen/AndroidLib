package com.zpw.stock.stock

import androidx.lifecycle.viewModelScope
import com.zpw.base.db.GZLL
import com.zpw.net.core.BaseViewModel
import com.zpw.net.core.ResponseLiveData
import com.zpw.net.core.ResponseMutableLiveData
import com.zpw.net.stock.GZLLResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StockViewModel : BaseViewModel<StockRepository>() {
    // 提供给Model层设置数据
    private val _insertAllGzllDbLiveData: ResponseMutableLiveData<Unit> = ResponseMutableLiveData()

    // 提供给View层观察数据
    val insertAllGzllDbLiveData: ResponseLiveData<Unit> = _insertAllGzllDbLiveData

    /**
     * 保存数据库国债
     */
    fun insertAllGZLL(gzll: List<GZLL>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAll(gzll, _insertAllGzllDbLiveData)
        }
    }

    // 提供给Model层设置数据
    private val _getAllGzllDbLiveData: ResponseMutableLiveData<List<GZLL>> = ResponseMutableLiveData()

    // 提供给View层观察数据
    val getAllGzllDbLiveData: ResponseLiveData<List<GZLL>> = _getAllGzllDbLiveData

    /**
     * 查询数据库国债
     */
    fun getAllGZLL() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.queryAllQZLLFromDB(_getAllGzllDbLiveData)
        }
    }

    // 提供给Model层设置数据
    private val _gzllLiveData: ResponseMutableLiveData<List<GZLLResponse>> = ResponseMutableLiveData()

    // 提供给View层观察数据
    val gzllLiveData: ResponseLiveData<List<GZLLResponse>> = _gzllLiveData

    /**
     * 查询国债
     *  @param  workTime 日期
     */
    fun queryChartInfo(workTime: String) {
        viewModelScope.launch {
            repository.queryChartInfo(workTime, _gzllLiveData)
        }
    }
}