package com.zpw.stock

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
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
        Thread {
            db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "database-gzll"
            ).build()
            dao = db.gzllDao()
            gzlls = dao.getAll() as ArrayList<GZLL>
            gzllList = mutableListOf()
            lineChart = findViewById(R.id.gzll_chart)
            runOnUiThread {
                if (gzlls.isEmpty()) {
                    loadGzllFromNet()
                } else {
                    var dateIndex = 0
                    gzlls.forEach {
                        gzllList.add(Entry(dateIndex++.toFloat(), it.price?.toFloat()!!))
                    }
                    invalidateChart()
                }
            }
        }.start()
    }

    fun loadGzllFromNet() {
        initDate()
        val gzllViewModel = ViewModelProvider(this).get(StockViewModel::class.java)
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
                    invalidateChart()
                }
            }
        })
    }

    private fun saveToDB() {
        Thread {
            dao.insertAll(gzlls)
        }.start()
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