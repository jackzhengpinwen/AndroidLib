package com.zpw.glkit.gl3samples.fragment

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zpw.glkit.R
import com.zpw.glkit.gl3samples.renderer.SampleTextureArrayRenderer

/**
 *
 *      这是一个纹理数组的例子，通用使用sampler2DArray将一组纹理传给fragment shader
 *      This sample demonstrates the usage of texture array. In the fragment shader, we use sampler2DArray to hold an array of texture.
 *
 **/

class SampleTextureArray : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_common_sample, container,  false)
        val glSurfaceView = rootView.findViewById<GLSurfaceView>(R.id.glsurfaceview)
        // 设置RGBA颜色缓冲、深度缓冲及stencil缓冲大小
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 0, 0)
        // 设置GL版本，这里设置为3.0
        glSurfaceView.setEGLContextClientVersion(3)
        // 设置对应sample的渲染器
        glSurfaceView.setRenderer(SampleTextureArrayRenderer())
        return rootView
    }
}