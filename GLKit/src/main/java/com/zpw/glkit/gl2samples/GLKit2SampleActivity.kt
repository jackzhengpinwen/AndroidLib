package com.zpw.glkit.gl2samples

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zpw.glkit.GlobalConstants
import com.zpw.glkit.R
import com.zpw.glkit.gl2samples.fragment.*

class GLKit2SimpleActivity : AppCompatActivity() {

    private val samples =
        arrayOf(
            SampleHelloWorld(),
            Sample2Triangles(),
            SampleVertexShader(),
            SampleFragmentShader(),
            SampleDrawMode(),
            SampleTexture(),
            SampleFrameBuffer()
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)
        title = intent.getStringExtra(GlobalConstants.KEY_SAMPLE_NAME)
        val sampleIndex = intent.getIntExtra(GlobalConstants.KEY_SAMPLE_INDEX, -1)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.content, samples[sampleIndex])
        transaction.commit()
    }

}
