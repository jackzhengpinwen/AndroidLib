package com.zpw.glkit.gl2samples.renderer

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.zpw.glkit.Util
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 *
 *      这是一个渲染纹理的例子
 *      This sample demonstrates how to use texture.
 *
 **/

class SampleTextureRenderer : GLSurfaceView.Renderer {

    /**
     * 我们是将点直接赋给了vertex shader中的gl_Position，这时相当于这些顶点的坐标是在设备标准化坐标系下的坐标，
     * 在设备标准化坐标系中，坐标原点在屏幕中间，x轴从原点指向右，y轴从原点指向上，x、y的取值范围都是-1~1
     */
    private val vertexShaderCode =
                "precision mediump float;\n" +
                "attribute vec4 a_position;\n" +
                "attribute vec2 a_textureCoordinate;\n" +
                "varying vec2 v_textureCoordinate;\n" +
                "void main() {\n" +
                "    v_textureCoordinate = a_textureCoordinate;\n" +
                "    gl_Position = a_position;\n" +
                "}"

    /**
     * 获取对应纹理坐标的纹理颜色，将颜色直接赋给了fragment shader中的gl_FragColor，相当于所有的片源颜色都是纹理的颜色
     */
    private val fragmentShaderCode =
                "precision mediump float;\n" +
                "varying vec2 v_textureCoordinate;\n" +
                "uniform sampler2D u_texture;\n" +
                "void main() {\n" +
                "    gl_FragColor = texture2D(u_texture, v_textureCoordinate);\n" +
                "}"

    // GLSurfaceView的宽高
    private var glSurfaceViewWidth = 0
    private var glSurfaceViewHeight = 0

    // 纹理顶点数据
    private val vertexData = floatArrayOf(-1f, -1f, -1f, 1f, 1f, 1f, -1f, -1f, 1f, 1f, 1f, -1f)
    private val VERTEX_COMPONENT_COUNT = 2
    private lateinit var vertexDataBuffer : FloatBuffer

    // 纹理坐标
    private val textureCoordinateData = floatArrayOf(0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 1f, 0f, 1f, 1f)
    private val TEXTURE_COORDINATE_COMPONENT_COUNT = 2
    private lateinit var textureCoordinateDataBuffer : FloatBuffer

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

        // 应用GL程序
        GLES20.glUseProgram(programId)

//        for (i in 0 until vertexData.size) {
//            vertexData[i] *= 5f
//        }

        // 将三角形顶点数据放入buffer中
        vertexDataBuffer = ByteBuffer.allocateDirect(vertexData.size * java.lang.Float.SIZE / 8)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexDataBuffer.put(vertexData)
        vertexDataBuffer.position(0)

        // 获取字段a_position在shader中的位置
        val aPositionLocation = GLES20.glGetAttribLocation(programId, "a_position")

        // 启动对应位置的参数
        GLES20.glEnableVertexAttribArray(aPositionLocation)

        // 指定a_position所使用的顶点数据
        GLES20.glVertexAttribPointer(aPositionLocation, VERTEX_COMPONENT_COUNT, GLES20.GL_FLOAT, false,0, vertexDataBuffer)

//        for (i in 0 until textureCoordinateData.size) {
//            textureCoordinateData[i] *= 3f
//        }

        // 将纹理坐标数据放入buffer中
        textureCoordinateDataBuffer = ByteBuffer.allocateDirect(textureCoordinateData.size * java.lang.Float.SIZE / 8)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        textureCoordinateDataBuffer.put(textureCoordinateData)
        textureCoordinateDataBuffer.position(0)

        // 获取字段a_textureCoordinate在shader中的位置
        val aTextureCoordinateLocation = GLES20.glGetAttribLocation(programId, "a_textureCoordinate")

        // 启动对应位置的参数
        GLES20.glEnableVertexAttribArray(aTextureCoordinateLocation)

        // 指定a_textureCoordinate所使用的顶点数据
        GLES20.glVertexAttribPointer(aTextureCoordinateLocation, TEXTURE_COORDINATE_COMPONENT_COUNT, GLES20.GL_FLOAT, false,0, textureCoordinateDataBuffer)

        // 创建图片纹理
        val textures = IntArray(1)
        GLES20.glGenTextures(textures.size, textures, 0)
        val imageTexture = textures[0]

        // 设置纹理参数
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, imageTexture)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)

        // 解码图片并加载到纹理中
        val bitmap = Util.decodeBitmapFromAssets("image_0.jpg")
        val b = ByteBuffer.allocate(bitmap.width * bitmap.height * 4)
        bitmap.copyPixelsToBuffer(b)
        b.position(0)
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bitmap.width, bitmap.height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, b)
        val uTextureLocation = GLES20.glGetAttribLocation(programId, "u_texture")
        GLES20.glUniform1i(uTextureLocation, 0)

    }

}