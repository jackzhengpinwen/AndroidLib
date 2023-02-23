package com.zpw.stock.stock

import com.zpw.net.core.BaseRepository
import com.zpw.net.core.ResponseMutableLiveData
import com.zpw.net.core.RetrofitManager
import com.zpw.net.stock.GZLLResponse

class StockRepository : BaseRepository() {
    /**
     * 查询国债期货
     *  @param  workTime 日期
     */
    suspend fun queryChartInfo(workTime: String, responseLiveData: ResponseMutableLiveData<List<GZLLResponse>>, showLoading: Boolean = true) {
        executeRequest({
            RetrofitManager.apiService.queryChartInfo(workTime)
        }, responseLiveData, showLoading)
    }
}