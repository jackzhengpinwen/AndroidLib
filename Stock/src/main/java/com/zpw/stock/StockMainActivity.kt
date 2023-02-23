package com.zpw.stock

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.zpw.net.core.ResponseObserver
import com.zpw.net.stock.GZLLResponse
import com.zpw.stock.stock.StockViewModel


class StockMainActivity : AppCompatActivity() {
    private val TAG = "StockMainActivity"
    lateinit var dateList: ArrayList<String>

    lateinit var gzllList: MutableList<Entry>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_main)
        initDate()
        val gzll_char = findViewById<LineChart>(R.id.gzll_chart)
        val gzllViewModel = ViewModelProvider(this).get(StockViewModel::class.java)
        gzllList = mutableListOf()
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
                    runOnUiThread {
                        val lineDataSet = LineDataSet(gzllList, "中债国债收益率曲线")
                        val lineData = LineData(lineDataSet)
                        gzll_char.data = lineData
                        gzll_char.invalidate()
                    }
                }
            }
        })
    }

    private fun initDate() {
        dateList = ArrayList()
        for (i in 2020 until 2021) {
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