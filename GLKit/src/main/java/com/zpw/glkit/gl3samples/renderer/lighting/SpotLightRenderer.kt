package com.zpw.glkit.gl3samples.renderer.lighting

import android.opengl.GLES30
import com.zpw.glkit.gl3samples.renderer.lighting.LightingRenderer
import javax.microedition.khronos.opengles.GL10

/**
 *
 *      θεδΎε­
 *      Spot light sample
 *
 **/

class SpotLightRenderer : LightingRenderer("lighting/spotlight.vs", "lighting/spotlightsoftedge.fs") {
    
    override fun onDrawFrame(gl: GL10?) {
        super.onDrawFrame(gl)
        GLES30.glUniform3f(GLES30.glGetUniformLocation(programId, "lightPos"), 2f, 0f, 0f)
    }

}