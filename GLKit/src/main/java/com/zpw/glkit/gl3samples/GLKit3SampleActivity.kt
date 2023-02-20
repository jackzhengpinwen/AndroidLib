package com.zpw.glkit.gl3samples

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zpw.glkit.GlobalConstants
import com.zpw.glkit.R
import com.zpw.glkit.gl3samples.fragment.*

class GLKit3SimpleActivity : AppCompatActivity() {

    private val samples =
        arrayOf(
            SampleShader(),
            SampleTextureArray(),
            SampleBinaryProgram(),
            SampleFenceSync(),
            SampleMultiRenderTarget(),
            SampleVBOAndIBO(),
            SampleEGL(),
            SampleMatrixTransform(),
            SampleColorBlend(),
            SampleLighting()
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
