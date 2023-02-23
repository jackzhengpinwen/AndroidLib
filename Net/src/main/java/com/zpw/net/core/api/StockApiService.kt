package com.zpw.net.core.api

import com.zpw.net.core.BaseResponse
import com.zpw.net.stock.GZLLResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface StockApiService {
    @GET("/cbweb-cbrc-web/cbrc/queryChartInfo")
    suspend fun queryChartInfo(
        @Query("workTime") workTime: String,
        @Query("locale") locale: String = "cn_ZH"
    ): BaseResponse<List<GZLLResponse>>
}