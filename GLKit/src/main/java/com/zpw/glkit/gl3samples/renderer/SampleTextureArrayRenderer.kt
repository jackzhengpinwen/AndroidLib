package com.zpw.glkit.gl3samples.renderer

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import com.zpw.glkit.Util
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 *
 *      这是一个纹理数组的例子，通用使用sampler2DArray将一组纹理传给fragment shader
 *      This sample demonstrates the usage of texture array. In the fragment shader, we use sampler2DArray to hold an array of texture.
 *
 **/

class SampleTextureArrayRenderer : GLSurfaceView.Renderer {

    private val vertexShaderCode =
                    "#version 300 es\n" +
                    "precision mediump float;\n" +
                    "layout(location = 0) in vec4 a_Position;\n" +
                    "layout(location = 1) in vec3 a_textureCoordinate;\n" +
                    "out vec3 v_textureCoordinate;\n" +
                    "void main() {\n" +
                    "    v_textureCoordinate = a_textureCoordinate;\n" +
                    "    gl_Position = a_Position;\n" +
                    "}"

    private val fragmentShaderCode =
                    "#version 300 es\n" +
                    "precision mediump float;\n" +
                    "precision mediump sampler2DArray;\n" +
                    "layout(location = 0) out vec4 fragColor;\n" +
                    "in vec3 v_textureCoordinate;\n" +
                    "// 注意这里用的是sampler2DArray而不是sampler2D\n" +
                    "// Note that the type of u_texture is sampler2DArray instead of sampler2D\n" +
                    "layout(location = 0) uniform sampler2DArray u_texture;\n" +
                    "void main() {\n" +
                    "    fragColor = texture(u_texture, v_textureCoordinate);\n" +
                    "}"

    // GLSurfaceView的宽高
    private var glSurfaceViewWidth = 0
    private var glSurfaceViewHeight = 0

    // 三角形顶点数据
    private val vertexData = floatArrayOf(-1f, -1f, -1f, 0f, 0f, 0f, -1f, -1f, 0f, 0f, 0f, -1f,
        0f, 0f, 0f, 1f, 1f, 1f, 0f, 0f, 1f, 1f, 1f, 0f)
    private val VERTEX_COMPONENT_COUNT = 2
    private lateinit var vertexDataBuffer : FloatBuffer

    // 纹理坐标
    private val textureCoordinateData = floatArrayOf(
                                            0f, 1f, 0f,
                                            0f, 0f, 0f,
                                            1f, 0f, 0f,
                                            0f, 1f, 0f,
                                            1f, 0f, 0f,
                                            1f, 1f, 0f,
                                            0f, 1f, 1f,
                                            0f, 0f, 1f,
                                            1f, 0f, 1f,
                                            0f, 1f, 1f,
                                            1f, 0f, 1f,
                                            1f, 1f, 1f
                                        )
    private val TEXTURE_COORDINATE_COMPONENT_COUNT = 3
    private lateinit var textureCoordinateDataBuffer : FloatBuffer

    // 要渲染的图片纹理
    private var imageTexture = 0

    // a_position、a_textureCoordinate和u_texture的位置，与shader中写的对应
    private val LOCATION_ATTRIBUTE_POSITION = 0
    private val LOCATION_ATTRIBUTE_TEXTURE_COORDINATE = 1
    private val LOCATION_UNIFORM_TEXTURE = 0

    override fun onDrawFrame(gl: GL10?) {

        // 设置清屏颜色
        GLES30.glClearColor(0.9f, 0.9f, 0.9f, 1f)

        // 清屏
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

        // 设置视口，这里设置为整个GLSurfaceView区域
        GLES30.glViewport(0, 0, glSurfaceViewWidth, glSurfaceViewHeight)

        // 设置好状态，准备渲染
        GLES30.glEnableVertexAttribArray(LOCATION_ATTRIBUTE_POSITION)
        GLES30.glVertexAttribPointer(LOCATION_ATTRIBUTE_POSITION, VERTEX_COMPONENT_COUNT, GLES30.GL_FLOAT, false,0, vertexDataBuffer)
        GLES30.glEnableVertexAttribArray(LOCATION_ATTRIBUTE_TEXTURE_COORDINATE)
        GLES30.glVertexAttribPointer(LOCATION_ATTRIBUTE_TEXTURE_COORDINATE, TEXTURE_COORDINATE_COMPONENT_COUNT, GLES30.GL_FLOAT, false,0, textureCoordinateDataBuffer)
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, imageTexture)

        // 调用draw方法用TRIANGLES的方式执行渲染，顶点数量为3个
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexData.size / VERTEX_COMPONENT_COUNT)

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        // 记录GLSurfaceView的宽高
        glSurfaceViewWidth = width
        glSurfaceViewHeight = height
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

        // 创建GL程序
        val programId = GLES30.glCreateProgram()

        // 加载、编译vertex shader和fragment shader
        val vertexShader = GLES30.glCreateShader(GLES30.GL_VERTEX_SHADER)
        val fragmentShader= GLES30.glCreateShader(GLES30.GL_FRAGMENT_SHADER)
        GLES30.glShaderSource(vertexShader, vertexShaderCode)
        GLES30.glShaderSource(fragmentShader, fragmentShaderCode)
        GLES30.glCompileShader(vertexShader)
        GLES30.glCompileShader(fragmentShader)

        // 将shader程序附着到GL程序上
        GLES30.glAttachShader(programId, vertexShader)
        GLES30.glAttachShader(programId, fragmentShader)

        val info1 = GLES30.glGetShaderInfoLog(fragmentShader)
        // 链接GL程序
        GLES30.glLinkProgram(programId)

        // 应用GL程序
        GLES30.glUseProgram(programId)

        // 将三角形顶点数据放入buffer中
        vertexDataBuffer = ByteBuffer.allocateDirect(vertexData.size * java.lang.Float.SIZE / 8)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexDataBuffer.put(vertexData)
        vertexDataBuffer.position(0)

        // 启动对应位置的参数，这里直接使用LOCATION_ATTRIBUTE_POSITION，而无需像OpenGL 2.0那样需要先获取参数的location
        GLES30.glEnableVertexAttribArray(LOCATION_ATTRIBUTE_POSITION)

        // 指定a_position所使用的顶点数据
        // Specify the data of a_position
        GLES30.glVertexAttribPointer(LOCATION_ATTRIBUTE_POSITION, VERTEX_COMPONENT_COUNT, GLES30.GL_FLOAT, false,0, vertexDataBuffer)

        // 将纹理坐标数据放入buffer中
        textureCoordinateDataBuffer = ByteBuffer.allocateDirect(textureCoordinateData.size * java.lang.Float.SIZE / 8)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        textureCoordinateDataBuffer.put(textureCoordinateData)
        textureCoordinateDataBuffer.position(0)

        // 启动对应位置的参数，这里直接使用LOCATION_ATTRIBUTE_TEXTURE_COORDINATE，而无需像OpenGL 2.0那样需要先获取参数的location
        GLES30.glEnableVertexAttribArray(LOCATION_ATTRIBUTE_TEXTURE_COORDINATE)

        // 指定a_textureCoordinate所使用的顶点数据
        GLES30.glVertexAttribPointer(LOCATION_ATTRIBUTE_TEXTURE_COORDINATE, TEXTURE_COORDINATE_COMPONENT_COUNT, GLES30.GL_FLOAT, false,0, textureCoordinateDataBuffer)

        // 创建图片纹理数组
        val textures = IntArray(1)
        GLES30.glGenTextures(textures.size, textures, 0)
        imageTexture = textures[0]

        // 注意这里是GL_TEXTURE_2D_ARRAY
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D_ARRAY, textures[0])
        GLES30.glTexStorage3D(GLES30.GL_TEXTURE_2D_ARRAY, 1, GLES30.GL_RGBA8, 390, 270, 2)

        // 通过glTexSubImage3D指定每层的纹理
        for (i in 0 until 2) {
            val bitmap = Util.decodeBitmapFromAssets("image_$i.jpg")
            val b = ByteBuffer.allocate(bitmap.width * bitmap.height * 4)
            bitmap.copyPixelsToBuffer(b)
            b.position(0)
            GLES30.glTexSubImage3D(GLES30.GL_TEXTURE_2D_ARRAY, 0, 0, 0, i, bitmap.width, bitmap.height, 1, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, b)
            bitmap.recycle()
        }

        // 启动对应位置的参数，这里直接使用LOCATION_UNIFORM_TEXTURE，而无需像OpenGL 2.0那样需要先获取参数的location
        GLES30.glUniform1i(LOCATION_UNIFORM_TEXTURE, 0)

    }

}