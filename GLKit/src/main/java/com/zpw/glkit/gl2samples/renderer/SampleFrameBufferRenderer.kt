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
 *      这是一个使用帧缓存的例子
 *      This sample demonstrates how to use frame buffer.
 *
 **/

class SampleFrameBufferRenderer : GLSurfaceView.Renderer {

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
     * 我们是将颜色直接赋给了fragment shader中的gl_FragColor，相当于所有的片源颜色都是我们设置的颜色
     */
    private val fragmentShaderCode0 =
                "precision mediump float;\n" +
                "varying vec2 v_textureCoordinate;\n" +
                "uniform sampler2D u_texture;\n" +
                "void main() {\n" +
                "    vec4 color = texture2D(u_texture, v_textureCoordinate);\n" +
                "    color.b = 0.5;\n" +
                "    gl_FragColor = color;\n" +
                "}"

    /**
     * 我们是将颜色直接赋给了fragment shader中的gl_FragColor，相当于所有的片源颜色都是我们设置的颜色
     */
    private val fragmentShaderCode1 =
                "precision mediump float;\n" +
                "varying vec2 v_textureCoordinate;\n" +
                "uniform sampler2D u_texture;\n" +
                "void main() {\n" +
                "    float offset = 0.005;\n" +
                "    vec4 color = texture2D(u_texture, v_textureCoordinate) * 0.11111;\n" +
                "    color += texture2D(u_texture, vec2(v_textureCoordinate.x - offset, v_textureCoordinate.y)) * 0.11111;\n" +
                "    color += texture2D(u_texture, vec2(v_textureCoordinate.x + offset, v_textureCoordinate.y)) * 0.11111;\n" +
                "    color += texture2D(u_texture, vec2(v_textureCoordinate.x - offset * 2.0, v_textureCoordinate.y)) * 0.11111;\n" +
                "    color += texture2D(u_texture, vec2(v_textureCoordinate.x + offset * 2.0, v_textureCoordinate.y)) * 0.11111;\n" +
                "    color += texture2D(u_texture, vec2(v_textureCoordinate.x - offset * 3.0, v_textureCoordinate.y)) * 0.11111;\n" +
                "    color += texture2D(u_texture, vec2(v_textureCoordinate.x + offset * 3.0, v_textureCoordinate.y)) * 0.11111;\n" +
                "    color += texture2D(u_texture, vec2(v_textureCoordinate.x - offset * 4.0, v_textureCoordinate.y)) * 0.11111;\n" +
                "    color += texture2D(u_texture, vec2(v_textureCoordinate.x + offset * 4.0, v_textureCoordinate.y)) * 0.11111;\n" +
                "    gl_FragColor = color;\n" +
                "}"

    // GLSurfaceView的宽高
    private var glSurfaceViewWidth = 0
    private var glSurfaceViewHeight = 0

    // 纹理顶点数据
    private val vertexData = floatArrayOf(-1f, -1f, -1f, 1f, 1f, 1f, -1f, -1f, 1f, 1f, 1f, -1f)
    private val VERTEX_COMPONENT_COUNT = 2
    private lateinit var vertexDataBuffer : FloatBuffer

    // 纹理坐标
    private val textureCoordinateData0 = floatArrayOf(0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 1f, 0f, 1f, 1f)
    private val textureCoordinateData1 = floatArrayOf(0f, 0f, 0f, 1f, 1f, 1f, 0f, 0f, 1f, 1f, 1f, 0f)
    private lateinit var textureCoordinateDataBuffer0 : FloatBuffer
    private lateinit var textureCoordinateDataBuffer1 : FloatBuffer
    private val TEXTURE_COORDINATE_COMPONENT_COUNT = 2

    // 2个GL Program
    private var programId0 = 0
    private var programId1 = 0

    // 帧缓存
    private var frameBuffer = 0

    // 帧缓绑定的texture
    private var frameBufferTexture = 0

    // 图片texture
    private var imageTexture = 0


    override fun onDrawFrame(gl: GL10?) {

        // 绑定第0个GL Program
        bindGLProgram(programId0, imageTexture, textureCoordinateDataBuffer0)

        // 绑定frame buffer
        bindFrameBuffer(frameBuffer)

        // 执行渲染，渲染效果为将图片的蓝色通道全部设为0.5
        render()

        // 绑定第1个GL Program
        bindGLProgram(programId1, frameBufferTexture, textureCoordinateDataBuffer1)

        // 绑定0号frame buffer
        bindFrameBuffer(0)

        // 执行渲染，渲染效果水平方向的模糊
        render()

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

        // 记录GLSurfaceView的宽高
        glSurfaceViewWidth = width
        glSurfaceViewHeight = height


        // 初始化frame buffer
        initFrameBuffer(width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

        // 初始化坐标、图片数据
        initData()

        // 创建2个GL Program，第一个将图片的蓝色通道全部设为0.5，第二做水平方向模糊
        programId0 = createGLProgram(vertexShaderCode, fragmentShaderCode0)
        programId1 = createGLProgram(vertexShaderCode, fragmentShaderCode1)

    }

    private fun initData() {

        // 将三角形顶点数据放入buffer中
        vertexDataBuffer = ByteBuffer.allocateDirect(vertexData.size * java.lang.Float.SIZE / 8)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
        vertexDataBuffer.put(vertexData)
        vertexDataBuffer.position(0)

        // 将纹理坐标数据放入buffer中
        textureCoordinateDataBuffer0 = ByteBuffer.allocateDirect(textureCoordinateData0.size * java.lang.Float.SIZE / 8)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
        textureCoordinateDataBuffer0.put(textureCoordinateData0)
        textureCoordinateDataBuffer0.position(0)

        textureCoordinateDataBuffer1 = ByteBuffer.allocateDirect(textureCoordinateData1.size * java.lang.Float.SIZE / 8)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
        textureCoordinateDataBuffer1.put(textureCoordinateData1)
        textureCoordinateDataBuffer1.position(0)

        // 创建图片纹理
        val textures = IntArray(1)
        GLES20.glGenTextures(textures.size, textures, 0)
        imageTexture = textures[0]

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

    }

    private fun initFrameBuffer(width : Int, height : Int) {
        
        // 创建frame buffer绑定的纹理
        val textures = IntArray(1)
        GLES20.glGenTextures(textures.size, textures, 0)
        frameBufferTexture = textures[0]

        // 创建frame buffer
        val frameBuffers = IntArray(1)
        GLES20.glGenFramebuffers(frameBuffers.size, frameBuffers, 0)
        frameBuffer = frameBuffers[0]

        // 将frame buffer与texture绑定
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameBufferTexture)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer)
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, frameBufferTexture, 0)
    }

    private fun createGLProgram(vertexShaderCode : String, fragmentShaderCode : String) : Int {

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

        Util.checkGLError()

        return programId

    }

    private fun bindGLProgram(programId : Int, texture : Int, textureCoordinateDataBuffer : FloatBuffer) {

        // 应用GL程序
        GLES20.glUseProgram(programId)

        // 获取字段a_position在shader中的位置
        val aPositionLocation = GLES20.glGetAttribLocation(programId, "a_position")

        // 启动对应位置的参数
        GLES20.glEnableVertexAttribArray(aPositionLocation)

        // 指定a_position所使用的顶点数据
        GLES20.glVertexAttribPointer(aPositionLocation, VERTEX_COMPONENT_COUNT, GLES20.GL_FLOAT, false,0, vertexDataBuffer)

        // 获取字段a_textureCoordinate在shader中的位置
        val aTextureCoordinateLocation = GLES20.glGetAttribLocation(programId, "a_textureCoordinate")

        // 启动对应位置的参数
        GLES20.glEnableVertexAttribArray(aTextureCoordinateLocation)

        // 指定a_textureCoordinate所使用的顶点数据
        GLES20.glVertexAttribPointer(aTextureCoordinateLocation, TEXTURE_COORDINATE_COMPONENT_COUNT, GLES20.GL_FLOAT, false,0, textureCoordinateDataBuffer)

        // 绑定纹理并设置u_texture参数
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture)
        val uTextureLocation = GLES20.glGetAttribLocation(programId, "u_texture")
        GLES20.glUniform1i(uTextureLocation, 0)

    }

    private fun bindFrameBuffer(frameBuffer : Int) {

        // 绑定frame buffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer)

    }

    private fun render() {

        // 设置清屏颜色
        GLES20.glClearColor(0.9f, 0.9f, 0.9f, 1f)

        // 清屏
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // 设置视口，这里设置为整个GLSurfaceView区域
        GLES20.glViewport(0, 0, glSurfaceViewWidth, glSurfaceViewHeight)

        // 调用draw方法用TRIANGLES的方式执行渲染，顶点数量为3个
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexData.size / VERTEX_COMPONENT_COUNT)

    }

}