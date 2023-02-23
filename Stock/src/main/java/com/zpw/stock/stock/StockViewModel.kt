package com.zpw.stock.stock

import androidx.lifecycle.viewModelScope
import com.zpw.net.core.BaseViewModel
import com.zpw.net.core.ResponseLiveData
import com.zpw.net.core.ResponseMutableLiveData
import com.zpw.net.stock.GZLLResponse
import kotlinx.coroutines.launch

class StockViewModel : BaseViewModel<StockRepository>() {
    // 提供给Model层设置数据
    private val _gzllLiveData: ResponseMutableLiveData<List<GZLLResponse>> = ResponseMutableLiveData()

    // 提供给View层观察数据
    val gzllLiveData: ResponseLiveData<List<GZLLResponse>> = _gzllLiveData

    /**
     * 查询国债期货
     *  @param  workTime 日期
     */
    fun queryChartInfo(workTime: String) {
        viewModelScope.launch {
            repository.queryChartInfo(workTime, _gzllLiveData)
        }
    }
}