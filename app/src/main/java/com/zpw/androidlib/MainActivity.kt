package com.zpw.androidlib

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zpw.glkit.gl2samples.GLKit2MainActivity
import com.zpw.glkit.gl3samples.GLKit3MainActivity
import com.zpw.net.NetMainActivity

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_main)
        val moduleList = findViewById<RecyclerView>(R.id.module_item)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        moduleList.layoutManager = linearLayoutManager
        moduleList.adapter = MyAdapter()
    }

    inner class MyAdapter : RecyclerView.Adapter<VH>() {

        private val moduleName = arrayOf(
            getString(R.string.module_gl2),
            getString(R.string.module_gl3),
            getString(R.string.module_net)
        )
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_module_list_item, null)
            return VH(view)
        }

        override fun getItemCount(): Int {
            return moduleName.size
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.button.text = moduleName[position]
            holder.button.setOnClickListener {
                if (position == 0) {
                    val intent = Intent(this@MainActivity, GLKit2MainActivity::class.java)
                    this@MainActivity.startActivity(intent)
                } else if (position == 1) {
                    val intent = Intent(this@MainActivity, GLKit3MainActivity::class.java)
                    this@MainActivity.startActivity(intent)
                } else if (position == 2) {
                    val intent = Intent(this@MainActivity, NetMainActivity::class.java)
                    this@MainActivity.startActivity(intent)
                }
            }
        }

    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val button = itemView.findViewById<Button>(R.id.button)
    }
}