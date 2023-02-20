package com.zpw.glkit.gl2samples

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zpw.glkit.GlobalConstants
import com.zpw.glkit.R
import com.zpw.glkit.Util

class GLKit2MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Util.context = applicationContext
        setContentView(R.layout.activity_main)
        val samplesList = findViewById<RecyclerView>(R.id.list)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        samplesList.layoutManager = layoutManager
        samplesList.adapter = MyAdapter()
    }

    inner class MyAdapter : RecyclerView.Adapter<VH>() {

        private val sampleNames =
            arrayOf(resources.getString(R.string.sample2_0),
                    resources.getString(R.string.sample2_1),
                    resources.getString(R.string.sample2_2),
                    resources.getString(R.string.sample2_3),
                    resources.getString(R.string.sample2_4),
                    resources.getString(R.string.sample2_5),
                    resources.getString(R.string.sample2_6))

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): VH {
            val view = LayoutInflater.from(p0.context).inflate(R.layout.layout_sample_list_item, p0, false)
            return VH(view)
        }

        override fun getItemCount(): Int {
            return sampleNames.size
        }

        override fun onBindViewHolder(p0: VH, p1: Int) {
            p0.button.text = sampleNames[p1]
            p0.button.setOnClickListener {
                val intent = Intent(this@GLKit2MainActivity, GLKit2SimpleActivity::class.java)
                intent.putExtra(GlobalConstants.KEY_SAMPLE_INDEX, p1)
                intent.putExtra(GlobalConstants.KEY_SAMPLE_NAME, sampleNames[p1])
                this@GLKit2MainActivity.startActivity(intent)
            }
        }

    }

    inner class VH(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var button : Button = itemView.findViewById(R.id.button)
    }
}
