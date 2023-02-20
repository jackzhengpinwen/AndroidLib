package com.zpw.glkit.gl3samples.renderer.lighting

import android.opengl.GLES30
import com.zpw.glkit.gl3samples.renderer.lighting.LightingRenderer
import javax.microedition.khronos.opengles.GL10

/**
 *
 *      点光例子
 *      Point light sample
 *
 **/

class PointLightRenderer : LightingRenderer("lighting/pointlight.vs", "lighting/pointlight.fs") {

    override fun onDrawFrame(gl: GL10?) {
        super.onDrawFrame(gl)
        GLES30.glUniform3f(GLES30.glGetUniformLocation(programId, "lightPos"), 5f, 0f, 0f)
    }

}