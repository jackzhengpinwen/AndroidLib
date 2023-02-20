package com.zpw.glkit.gl2samples.renderer

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 *
 *      在第一个例子Hello World的基础上稍作修改，渲染2个三角形的例子
 *      This sample demonstrates how to render 2 triangles base on the code of our first sample
 *
 **/

class Sample2TrianglesRenderer : GLSurfaceView.Renderer {

    /**
     * 我们是将点直接赋给了vertex shader中的gl_Position，这时相当于这些顶点的坐标是在设备标准化坐标系下的坐标，
     * 在设备标准化坐标系中，坐标原点在屏幕中间，x轴从原点指向右，y轴从原点指向上，x、y的取值范围都是-1~1
     */
    private val vertexShaderCode =
        "precision mediump float;\n" +
                "attribute vec4 a_Position;\n" +
                "void main() {\n" +
                "    gl_Position = a_Position;\n" +
                "}"

    /**
     * 我们是将颜色直接赋给了fragment shader中的gl_FragColor，相当于所有的片源颜色都是我们设置的颜色
     */
    private val fragmentShaderCode =
        "precision mediump float;\n" +
                "void main() {\n" +
                "    gl_FragColor = vec4(0.0, 1.0, 0.0, 1.0);\n" +
                "}"

    private var glSurfaceViewWidth = 0
    private var glSurfaceViewHeight = 0

    // 三角形顶点数据
    private val vertexData = floatArrayOf(-0.5f, 1f, -1f, 0f, 0f, 0f, 0.5f, 0f, 0f, -1f, 1f, -1f)

    // 每个顶点的成份数
    private val VERTEX_COMPONENT_COUNT = 2

    /**
     * onDrawFrame()是渲染时的回调，我们的渲染逻辑就是在这里面写
     */
    override fun onDrawFrame(gl: GL10?) {
        // 设置清屏颜色
        GLES20.glClearColor(0.9f, 0.9f, 0.9f, 1f)

        // 清屏
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // 设置视口，这里设置为整个GLSurfaceView区域
        GLES20.glViewport(0, 0, glSurfaceViewWidth, glSurfaceViewHeight)

        // 调用draw方法用TRIANGLES的方式执行渲染，顶点数量为3个
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexData.size / VERTEX_COMPONENT_COUNT)
    }

    /**
     * onSurfaceChanged()是在GLSurfaceView宽高改变时会回调，一般可以在这里记录最新的宽高
     */
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        // 记录GLSurfaceView的宽高
        glSurfaceViewWidth = width
        glSurfaceViewHeight = height
    }

    /**
     * onSurfaceCreated()在GLSurfaceView创建好时会回调，一般可以在里面写一些初始化逻辑
     */
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // 创建GL程序
        val programId = GLES20.glCreateProgram()

        // 加载、编译vertex shader和fragment shader
        val vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
        val fragmentShader= GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
        GLES20.glShaderSource(vertexShader, vertexShaderCode)
        GLES20.glShaderSource(fragmentShader, fragmentShaderCode)
        GLES20.glCompileShader(vertexShader)
        GLES20.glCompileShader(fragmentShader)

        // 将shader程序附着到GL程序上
        GLES20.glAttachShader(programId, vertexShader)
        GLES20.glAttachShader(programId, fragmentShader)

        // 链接GL程序
        GLES20.glLinkProgram(programId)

        // 将三角形顶点数据放入buffer中
        val buffer = ByteBuffer.allocateDirect(vertexData.size * java.lang.Float.SIZE)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        buffer.put(vertexData)
        buffer.position(0)

        // 应用GL程序
        GLES20.glUseProgram(programId)

        // 获取字段a_Position在shader中的位置
        val location = GLES20.glGetAttribLocation(programId, "a_Position")

        // 启动对应位置的参数
        GLES20.glEnableVertexAttribArray(location)

        // 指定a_Position所使用的顶点数据
        GLES20.glVertexAttribPointer(location, VERTEX_COMPONENT_COUNT, GLES20.GL_FLOAT, false,0, buffer)
    }

}