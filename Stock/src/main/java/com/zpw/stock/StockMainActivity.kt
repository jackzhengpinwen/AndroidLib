package com.zpw.stock

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.zpw.base.db.AppDatabase
import com.zpw.base.db.GZLL
import com.zpw.base.db.GZLLDao
import com.zpw.net.core.ResponseObserver
import com.zpw.net.stock.GZLLResponse
import com.zpw.stock.stock.StockViewModel


class StockMainActivity : AppCompatActivity() {
    private val TAG = "StockMainActivity"
    lateinit var gzllViewModel: StockViewModel
    lateinit var lineChart: LineChart
    lateinit var dateList: ArrayList<String>
    lateinit var gzllList: MutableList<Entry>
    lateinit var db: AppDatabase
    lateinit var dao: GZLLDao
    lateinit var gzlls: ArrayList<GZLL>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_main)
        lineChart = findViewById(R.id.gzll_chart)
        gzllViewModel = ViewModelProvider(this).get(StockViewModel::class.java)
        // 先从数据库查询有没有缓存
        gzllViewModel.getAllGZLL()
        gzllViewModel.getAllGzllDbLiveData.observe(this, object : ResponseObserver<List<GZLL>>() {
            override fun onSuccess(data: List<GZLL>?) {
                data?.let {
                    if (it.isEmpty()) { // 没缓存，从网络获取
                        loadGzllFromNet()
                    } else { // 有缓存，直接加载
                        gzlls = ArrayList(data)
                        gzllList = mutableListOf()
                        var dateIndex = 0
                        gzlls.forEach {
                            gzllList.add(Entry(dateIndex++.toFloat(), it.price?.toFloat()!!))
                        }
                        invalidateChart()
                    }
                }
            }

        })
    }

    fun loadGzllFromNet() {
        initDate()
        var dateIndex = 0
        gzllViewModel.queryChartInfo(dateList.get(dateIndex))
        gzllViewModel.gzllLiveData.observe(this, object : ResponseObserver<List<GZLLResponse>>() {
            override fun onSuccess(data: List<GZLLResponse>?) {
                data?.let {
                    if (data.isNotEmpty()) {
                        data.forEach  { gzllResponse ->
                            if (gzllResponse.ycDefName.equals("中债国债收益率曲线")) {
                                if (gzllResponse.seriesData.isNotEmpty()) {
                                    gzllResponse.seriesData.forEach { price ->
                                        if (price.size == 2) {
                                            if (price[0] == 10.0) {
                                                if (gzllList == null) {
                                                    gzllList = mutableListOf()
                                                }
                                                gzllList.add(Entry(dateIndex.toFloat(), price[1].toFloat()))
                                                if (gzlls == null) {
                                                    gzlls = ArrayList()
                                                }
                                                gzlls.add(GZLL(dateIndex, dateList.get(dateIndex), price[1]))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                dateIndex++
                if (dateIndex < dateList.size) {
                    gzllViewModel.queryChartInfo(dateList.get(dateIndex))
                } else {
                    saveToDB()
                }
            }
        })
    }

    private fun saveToDB() {
        gzllViewModel.insertAllGZLL(gzlls)
        gzllViewModel.insertAllGzllDbLiveData.observe(this, object : ResponseObserver<Unit>() {
            override fun onSuccess(data: Unit?) {
                invalidateChart()
            }

        })
    }

    private fun invalidateChart() {
        runOnUiThread {
            val lineDataSet = LineDataSet(gzllList, "中债国债收益率曲线")
            val lineData = LineData(lineDataSet)
            lineChart.data = lineData
            lineChart.invalidate()
        }
    }

    private fun initDate() {
        dateList = ArrayList()
        for (i in 2020 until 2024) {
            val sb = StringBuilder()
            for (j in 1 until 13) {
                for (k in 1 until 32) {
                    sb.append(i).append("-")
                    if (j < 10) {
                        sb.append("0")
                    }
                    sb.append(j).append("-")
                    if (k < 10) {
                        sb.append("0")
                    }
                    sb.append(k)
                    dateList.add(sb.toString())
                    sb.clear()
                }
            }
        }
    }
}