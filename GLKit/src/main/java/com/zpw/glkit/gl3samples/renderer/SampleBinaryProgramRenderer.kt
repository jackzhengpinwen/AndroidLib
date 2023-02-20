package com.zpw.glkit.gl3samples.renderer

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Environment
import com.zpw.glkit.Util
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 *
 *      这是一个使用二进制GL program的例子，演示将link好的GL Program以文件的方式保存，以及读取GL program文件并加载
 *      This sample demonstrates the usage of binary GL program. We can save the linked GL program to file and load a binary GL program from file.
 *
 **/

class SampleBinaryProgramRenderer : GLSurfaceView.Renderer {

    private val vertexShaderCode =
        "#version 300 es\n" +
        "precision mediump float;\n" +
        "layout(location = 0) in vec4 a_position;\n" +
        "layout(location = 1) in vec2 a_textureCoordinate;\n" +
        "out vec2 v_textureCoordinate;\n" +
        "void main() {\n" +
        "    v_textureCoordinate = a_textureCoordinate;\n" +
        "    gl_Position = a_position;\n" +
        "}"

    private val fragmentShaderCode =
        "#version 300 es\n" +
        "precision mediump float;\n" +
        "layout(location = 0) out vec4 fragColor;\n" +
        "in vec2 v_textureCoordinate;\n" +
        "uniform sampler2D u_texture;\n" +
        "void main() {\n" +
        "    fragColor = texture(u_texture, v_textureCoordinate);\n" +
        "}"

    // GLSurfaceView的宽高
    private var glSurfaceViewWidth = 0
    private var glSurfaceViewHeight = 0

    // 三角形顶点数据
    private val vertexData = floatArrayOf(-1f, -1f, -1f, 1f, 1f, 1f, -1f, -1f, 1f, 1f, 1f, -1f)
    private val VERTEX_COMPONENT_COUNT = 2
    private lateinit var vertexDataBuffer : FloatBuffer

    // 纹理坐标
    private val textureCoordinateData = floatArrayOf(0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 1f, 0f, 1f, 1f)
    private val TEXTURE_COORDINATE_COMPONENT_COUNT = 2
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
        var programId = GLES30.glCreateProgram()

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

        // 链接GL程序
        GLES30.glLinkProgram(programId)

        // 将链接好的二进制GL program保存到文件中
        val binaryBuffer = ByteBuffer.allocate(65536)
        val lengthBuffer = IntBuffer.allocate(1)
        val formatBuffer = IntBuffer.allocate(1)
        GLES30.glGetProgramBinary(programId, binaryBuffer.capacity(), lengthBuffer, formatBuffer, binaryBuffer)
        saveGLProgramBinary(binaryBuffer, lengthBuffer[0], formatBuffer[0])
        GLES30.glDeleteProgram(programId)

        // 从文件中读取进二进制GL program并加载
        val glProgramBinary = getGLProgramBinary()
        programId = GLES30.glCreateProgram()
        GLES30.glProgramBinary(programId, glProgramBinary.format, glProgramBinary.binaryBuffer, glProgramBinary.length)

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

        // 创建图片纹理
        val textures = IntArray(1)
        GLES30.glGenTextures(textures.size, textures, 0)
        imageTexture = textures[0]

        // 将图片解码并加载到纹理中
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        val bitmap = Util.decodeBitmapFromAssets("image_0.jpg")
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, imageTexture)
        val b = ByteBuffer.allocate(bitmap.width * bitmap.height * 4)
        bitmap.copyPixelsToBuffer(b)
        b.position(0)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE)
        GLES30.glTexImage2D(
            GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, bitmap.width,
            bitmap.height, 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, b)
        bitmap.recycle()

        // 启动对应位置的参数，这里直接使用LOCATION_UNIFORM_TEXTURE，而无需像OpenGL 2.0那样需要先获取参数的location
        GLES30.glUniform1i(LOCATION_UNIFORM_TEXTURE, 0)

    }

    private val txtFilePath = "length_and_format.txt"
    private val binFilePath = "program_binary.bin"

    private fun saveGLProgramBinary(binaryBuffer : ByteBuffer, length : Int, format : Int) {
        val fileLengthAndFormat = File(getFilePath(txtFilePath))
        fileLengthAndFormat.writeText(length.toString())
        fileLengthAndFormat.appendText("\n")
        fileLengthAndFormat.appendText(format.toString())
        val fileProgramBinary = File(getFilePath(binFilePath))
        val bytes = ByteArray(length)
        binaryBuffer.get(bytes)
        fileProgramBinary.writeBytes(bytes)
    }

    private fun getGLProgramBinary() : GLProgramBinary {
        val fileLengthAndFormat = File(getFilePath(txtFilePath))
        val lines = fileLengthAndFormat.readLines()
        val fileProgramBinary = File(getFilePath(binFilePath))
        return GLProgramBinary(ByteBuffer.wrap(fileProgramBinary.readBytes()), lines[0].toInt(), lines[1].toInt())
    }

    private fun getFilePath(subFilePath : String): String {
        var path = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
            Util.context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath + File.separator + subFilePath
        } else {
            Environment.getExternalStorageDirectory()?.absolutePath + File.separator + subFilePath
        }
        return path
    }

    class GLProgramBinary(val binaryBuffer : ByteBuffer, val length : Int, val format : Int)

}