package com.zpw.glkit.gl3samples.fragment

import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.zpw.glkit.R
import com.zpw.glkit.gl3samples.renderer.lighting.BumpedLightRenderer
import com.zpw.glkit.gl3samples.renderer.lighting.DirectionalLightRenderer
import com.zpw.glkit.gl3samples.renderer.lighting.PointLightRenderer
import com.zpw.glkit.gl3samples.renderer.lighting.SpotLightRenderer

/**
 *
 *      光照例子，包括平行光、点光、聚光和法向图
 *      Lighting samples, including directional light, point light, spot light and bumped
 *
 **/

class SampleLighting : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sample_lighting, container,  false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val glSurfaceViewContainer = view.findViewById<FrameLayout>(R.id.glSurfaceViewContainer)
        val directionalLight = view.findViewById<Button>(R.id.directionalLight)
        val pointLight = view.findViewById<Button>(R.id.pointLight)
        val spotLight = view.findViewById<Button>(R.id.spotLight)
        val bumped = view.findViewById<Button>(R.id.bumped)
        directionalLight.setOnClickListener {
            updateSample(glSurfaceViewContainer, DirectionalLightRenderer())
        }
        pointLight.setOnClickListener {
            updateSample(glSurfaceViewContainer, PointLightRenderer())
        }
        spotLight.setOnClickListener {
            updateSample(glSurfaceViewContainer, SpotLightRenderer())
        }
        bumped.setOnClickListener {
            updateSample(glSurfaceViewContainer, BumpedLightRenderer())
        }
    }

    private fun updateSample(rootView: View, renderer: GLSurfaceView.Renderer) {
        rootView as ViewGroup
        rootView.removeAllViews()
        rootView.addView(createGLSurfaceView(rootView.context, renderer))
    }

    private fun createGLSurfaceView(context: Context, renderer: GLSurfaceView.Renderer): GLSurfaceView {
        val glSurfaceView = GLSurfaceView(context)
        glSurfaceView.setEGLContextClientVersion(3)
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 8, 0)
        glSurfaceView.setRenderer(renderer)
        return glSurfaceView
    }

}