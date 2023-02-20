package com.zpw.glkit.gl2samples.fragment

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zpw.glkit.R
import com.zpw.glkit.gl2samples.renderer.SampleDrawModeRenderer

/**
 *
 *      这是一个在SampleDrawModeRenderer中演示不同绘制模式的例子
 *      This sample demonstrates the difference between the draw modes in SampleDrawModeRenderer
 *
 **/

class SampleDrawMode : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_sample_draw_mode, container,  false)
        val glSurfaceView = rootView.findViewById<GLSurfaceView>(R.id.glsurfaceview)
        // 设置RGBA颜色缓冲、深度缓冲及stencil缓冲大小
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 0, 0)
        // 设置GL版本，这里设置为2.0
        glSurfaceView.setEGLContextClientVersion(2)
        // 设置对应sample的渲染器
        glSurfaceView.setRenderer(SampleDrawModeRenderer(rootView))
        return rootView
    }
}