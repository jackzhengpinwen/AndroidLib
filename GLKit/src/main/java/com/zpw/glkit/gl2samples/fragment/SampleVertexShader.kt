package com.zpw.glkit.gl2samples.fragment

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kenneycode.samples.renderer.SampleVertexShaderRenderer
import com.zpw.glkit.R

/**
 *
 *      这是一个在SampleVertexShaderRenderer使用vertex shader做顶点变换的例子，例子中将演示平移、缩放和旋转变换
 *      This sample demonstrates how to do vertex translation，scale and rotation using vertex shader in SampleVertexShaderRenderer
 *
 **/

class SampleVertexShader : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_common_sample, container,  false)
        val glSurfaceView = rootView.findViewById<GLSurfaceView>(R.id.glsurfaceview)
        // 设置RGBA颜色缓冲、深度缓冲及stencil缓冲大小
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 0, 0)
        // 设置GL版本，这里设置为2.0
        glSurfaceView.setEGLContextClientVersion(2)
        // 设置对应sample的渲染器
        glSurfaceView.setRenderer(SampleVertexShaderRenderer())
        return rootView
    }
}