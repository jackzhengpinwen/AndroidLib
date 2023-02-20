package com.zpw.glkit.gl2samples.renderer

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 *
 *      这是一个利用fragment shader渲染彩色三角形例子
 *      This is a sample of using fragment shader to render a colorful triangle
 *
 **/

class SampleFragmentShaderRenderer : GLSurfaceView.Renderer {

    /**
     * 在顶点shader中定义颜色变量 a_Color，并赋值给 v_Color
     * 我们是将点直接赋给了vertex shader中的gl_Position，这时相当于这些顶点的坐标是在设备标准化坐标系下的坐标，
     * 在设备标准化坐标系中，坐标原点在屏幕中间，x轴从原点指向右，y轴从原点指向上，x、y的取值范围都是-1~1
     */
    private val vertexShaderCode =
        "precision mediump float;\n" +
                "attribute vec4 a_Position;\n" +
                "attribute vec4 a_Color;\n" +
                "varying vec4 v_Color;\n" +
                "void main() {\n" +
                "    v_Color = a_Color;\n" +
                "    gl_Position = a_Position;\n" +
                "}"

    /**
     * 我们是将vertex 中传递的 v_Color 颜色变量直接赋给了fragment shader中的gl_FragColor，相当于所有的片源颜色都是我们设置的颜色
     */
    private val fragmentShaderCode =
        "precision mediump float;\n" +
                "varying vec4 v_Color;\n" +
                "void main() {\n" +
                "    gl_FragColor = v_Color;\n" +
                "}"

    private var glSurfaceViewWidth = 0
    private var glSurfaceViewHeight = 0

    // 三角形顶点数据
    private val vertexData = floatArrayOf(0f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f)


    private val colorData = floatArrayOf(
                                        1.0f, 0.0f, 0.0f, 1.0f,
                                        0.0f, 1.0f, 0.0f, 1.0f,
                                        0.0f, 0.0f, 1.0f, 1.0f)

    // 每个顶点的成份数
    private val VERTEX_COMPONENT_COUNT = 2

    // 每个颜色的成份数（RGBA）
    private val COLOR_COMPONENT_COUNT = 4

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

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        // 记录GLSurfaceView的宽高
        glSurfaceViewWidth = width
        glSurfaceViewHeight = height
    }

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
        val vertexDataBuffer = ByteBuffer.allocateDirect(vertexData.size * java.lang.Float.SIZE)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexDataBuffer.put(vertexData)
        vertexDataBuffer.position(0)

        // 应用GL程序
        GLES20.glUseProgram(programId)

        // 获取字段a_Position在shader中的位置
        val aPositionLocation = GLES20.glGetAttribLocation(programId, "a_Position")

        // 启动对应位置的参数
        GLES20.glEnableVertexAttribArray(aPositionLocation)

        // 指定a_Position所使用的顶点数据
        GLES20.glVertexAttribPointer(aPositionLocation, VERTEX_COMPONENT_COUNT, GLES20.GL_FLOAT, false,0, vertexDataBuffer)

        // 将颜色数据放入buffer中
        val colorDataBuffer = ByteBuffer.allocateDirect(colorData.size * java.lang.Float.SIZE)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        colorDataBuffer.put(colorData)
        colorDataBuffer.position(0)

        // 获取字段a_Color在shader中的位置
        val aColorLocation = GLES20.glGetAttribLocation(programId, "a_Color")

        // 启动对应位置的参数
        GLES20.glEnableVertexAttribArray(aColorLocation)

        // 指定a_Color所使用的顶点数据
        GLES20.glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GLES20.GL_FLOAT, false,0, colorDataBuffer)
    }

}