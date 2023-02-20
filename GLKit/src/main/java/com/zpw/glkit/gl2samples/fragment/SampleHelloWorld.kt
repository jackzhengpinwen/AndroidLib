package com.zpw.glkit.gl2samples.fragment

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zpw.glkit.R
import com.zpw.glkit.gl2samples.renderer.SampleHelloWorldRenderer

/**
 *
 *      这是一个Hello World例子，通过SampleHelloWorldRenderer渲染一个最简单的三角形
 *      This is the first sample Hello World, and it will show you how to render a simplest triangle in SampleHelloWorldRenderer
 *
 **/

public class SampleHelloWorld : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_common_sample, container,  false)
        // 要在Android上进行OpenGL渲染，首先要有GL环境，Android的GLSurfaceView，它就自带了GL环境
        val glSurfaceView = rootView.findViewById<GLSurfaceView>(R.id.glsurfaceview)
        // 设置RGBA颜色缓冲、深度缓冲及stencil缓冲大小
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 0, 0)
        // 设置GL版本，这里设置为2.0
        glSurfaceView.setEGLContextClientVersion(2)
        // 设置对应sample的渲染器
        glSurfaceView.setRenderer(SampleHelloWorldRenderer())
        return rootView
    }
}