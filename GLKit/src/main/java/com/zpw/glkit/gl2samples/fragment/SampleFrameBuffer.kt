package com.zpw.glkit.gl2samples.fragment

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zpw.glkit.gl2samples.renderer.SampleFrameBufferRenderer
import com.zpw.glkit.R

/**
 *
 *      这是一个使用帧缓存的例子
 *      This sample demonstrates how to use frame buffer.
 *
 **/

class SampleFrameBuffer : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_common_sample, container,  false)
        val glSurfaceView = rootView.findViewById<GLSurfaceView>(R.id.glsurfaceview)
        // 设置RGBA颜色缓冲、深度缓冲及stencil缓冲大小
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 0, 0)
        // 设置GL版本，这里设置为2.0
        glSurfaceView.setEGLContextClientVersion(2)
        // 设置对应sample的渲染器
        glSurfaceView.setRenderer(SampleFrameBufferRenderer())
        return rootView
    }
}