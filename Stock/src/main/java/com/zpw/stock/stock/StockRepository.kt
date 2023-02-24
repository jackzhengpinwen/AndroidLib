package com.zpw.stock.stock

import com.zpw.base.db.AppDatabase
import com.zpw.base.db.GZLL
import com.zpw.net.core.BaseRepository
import com.zpw.net.core.ResponseLiveData
import com.zpw.net.core.ResponseMutableLiveData
import com.zpw.net.core.RetrofitManager
import com.zpw.net.stock.GZLLResponse

class StockRepository : BaseRepository() {
    /**
     * 查询国债期货
     *  @param  workTime 日期
     */
    suspend fun queryChartInfo(workTime: String, responseLiveData: ResponseMutableLiveData<List<GZLLResponse>>, showLoading: Boolean = true) {
        executeNetRequest({
            RetrofitManager.apiService.queryChartInfo(workTime)
        }, responseLiveData, showLoading)
    }

    suspend fun queryAllQZLLFromDB(responseLiveData: ResponseMutableLiveData<List<GZLL>>, showLoading: Boolean = true) {
        executeDBRequest({
            AppDatabase.getInstance().gzllDao().getAll()
        }, responseLiveData, showLoading)
    }

    suspend fun insertAll(gzll: List<GZLL>, responseLiveData: ResponseMutableLiveData<Unit>, showLoading: Boolean = true) {
        executeDBRequest({
            AppDatabase.getInstance().gzllDao().insertAll(gzll)
        }, responseLiveData, showLoading)
    }
}