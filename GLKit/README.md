# GLKit
加快Android OpenGL开发的库

# OpenGL ES 2.0
## OpenGL的渲染管线（pipeline）
我们调用OpenGL的drawXXX()方法后，我们传递的顶点首先会经过顶点着色器vertex shader的处理，一般会在里面做顶点变换相关的逻辑，
然后进行图元装配，再经过几何着色器geometry shader，这个着色器相对来说使用得少一些，
然后接下来就是光栅化，所谓光栅化就是把我们要渲染的图像打碎成屏幕上的像素，因为最终要显示到屏幕上，就必须将图形对应到像素上，
光栅化完成后，我们就有了要渲染的图形对应的像素，此时像素还没有颜色，需要我们填上颜色，
这时就到达到了片段着色器fragment shader，在fragment shader中我们通常进行颜色的计算，确定对应的像素显示什么颜色

在整个渲染管线中，vertex shader、geometry shader和fragment shader这三部分是可编程分部，可编写shader代码实现相应的功能。
我们的shader代码并不是像普通程序那样，一次性输入所有顶点，然后再输出，
例如对于vertex shader，我们传递了3个顶点，并不是3个顶点一起执行一次vertex shader，而是分别对这3个顶点执行一次，也就是执行了3次。
对于fragment shader也是类似的，并不是执行一次为所有的像素填充颜色，而是对每个像素都执行一次。

## vertex shader
```
precision mediump float;
attribute vec4 a_Position;
void main() {
    gl_Position = a_Position;
}
```
第一行，我们声明了这个shader使用的精度，mediump表示使用中等精度，一些对精度要求很高的应用，可以声明高精度。
第二行，我们声明了一个attribute vec4 a_Position变量，它表明这是一个attribute类型的四维向量，什么attribute类型？
上文提到如果我们传递了3个顶点，就会对这3个顶点分别执行一次vertex shader，在一次执行中，这个attribute类型的变量所对应的就是这3个顶点中的某个顶点，
与此相对的是uniform变量，uniform变量在所有vertex shader的执行过程中都是同一个值。
这里为什么一个顶点是四维向量？我们不是只传了二维的x、y坐标吗？在OpenGL中，顶点总是四维的，即x、y、z、w，其中x、y、z不传的话默认是0，w不传的话默认是1，w是用来做归一化（标准化）的.
第三行，vertex shader中包含一个main()方法作为入口，gl_Position是vertex shader的一个内置变量，表示vertex shader的输出，这里是直接将输入的顶点原样又输出了
```
gl_Position = a_Position + vec4(0.3, 0.3, 0, 0);
```
上面的算法就是将坐标进行右上偏移。

## fragment shader
fragment shader会在光栅化后，对每个像素执行一次。
```
precision mediump float;
void main() {
    gl_FragColor = vec4(0.0, 0.0, 1.0, 1.0);
}
```
在一开始声明了float的精度为中精度，接着和vertex shader一样有一个main()方法作为入口，fragment shader中有一个内置变量gl_FragColor表示fragment shader的输出，
在之前的例子我们给gl_FragColor设置了一个固定值vec4(0.0, 0.0, 1.0, 1.0)，这是一个RGBA的颜色值，因此我们之前看到的三角形是一个纯蓝色的

## 纹理
如果我们要渲染一张图片，该怎么做呢？这就需要用到纹理，我们需要创建一个纹理并把图片加载到纹理中，然后在fragment shader中对纹理进行采样，从而将纹理渲染出来。
```
// 创建图片纹理
// Create texture
val textures = IntArray(1)
GLES20.glGenTextures(textures.size, textures, 0)
val imageTexture = textures[0]

GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, imageTexture)
GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST)
GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST)
GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
```
创建好纹理之后，它还是空的，我们要给这个纹理填充内容，我们先将一张图片解码成bitmap，并将像素数据copy到一个ByteBuffer中，因为将bitmp加载到纹理中的方法接受的是一个ByteBuffer：
```
val bitmap = Util.decodeBitmapFromAssets("image_0.jpg")
val b = ByteBuffer.allocate(bitmap.width * bitmap.height * 4)
bitmap.copyPixelsToBuffer(b)
b.position(0)
```
通过glTexImage2D方法将上面得到的ByteBuffer加载到纹理中：
```
GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 
                    0, 
                    GLES20.GL_RGBA, 
                    bitmap.width, 
                    bitmap.height, 
                    0, 
                    GLES20.GL_RGBA, 
                    GLES20.GL_UNSIGNED_BYTE, 
                    b)
```
这时纹理中才真正有了内容，接下来需要将纹理传递给fragment shader进行采样，从而渲染出来，我们先来看看如何在fragment shader中使用纹理：
```
// vertex shader
precision mediump float;
attribute vec4 a_position;
attribute vec2 a_textureCoordinate;
varying vec2 v_textureCoordinate;
void main() {
    v_textureCoordinate = a_textureCoordinate;
    gl_Position = a_position;
}

// fragment shader
precision mediump float;
varying vec2 v_textureCoordinate;
uniform sampler2D u_texture;
void main() {
    gl_FragColor = texture2D(u_texture, v_textureCoordinate);
}
```
关键点是uniform sampler2D u_texture;这句，它声明了一个2D的采样器用于采样纹理。
gl_FragColor = texture2D(u_texture, v_textureCoordinate);就是从纹理中采样出v_textureCoordinate坐标所对应的颜色作为fragment shader的输出，
我们可以看到，fragment shader的输出实际上就是一个颜色。

纹理和shader都准备好了，如果把它们联系越来呢？首先需要像之前一样先获取shader中纹理变量的location：
```
val uTextureLocation = GLES20.glGetAttribLocation(programId, "u_texture")
```
然后给这个location指定对应哪个纹理单元，这里我们使用0号纹理单元：
```
GLES20.glUniform1i(uTextureLocation, 0)
```
纹理单元可以想像成是一种类似寄存器的东西，在OpenGL使用纹理前，我们先要把纹理放到某个纹理单元上，之后的操作OpenGL就会去我们指定的纹理单元上去取对应的纹理。
我们刚才让location对应0号纹理单元，但是我们好像没并没有哪里说我们把纹理放在了0号纹理单元，这是怎么回事呢？
因为默认情况下，OpenGL是使用0号纹理单元的，我们因为没有更改过使用的纹理单元，因此默认就是0号了，我们如果想使用其它纹理单元，可以通过glActiveTexture来指定，例如：
```
GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
```
就会指定使用1号纹理单元，如果我们的例子改为使用1号纹理单元，那么uTextureLocation就要相应地改为让它对应1号纹理单元：
```
GLES20.glUniform1i(uTextureLocation, 1)
```
下面我看来看看顶点坐标和纹理坐标：
```
private val vertexData = floatArrayOf(-1f, -1f, -1f, 1f, 1f, 1f, -1f, -1f, 1f, 1f, 1f, -1f)
private val textureCoordinateData = floatArrayOf(0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 1f, 0f, 1f, 1f)
```
我们的顶点坐标和纹理坐标上下是颠倒的，比如顶点坐标(-1,-1)对应纹理坐标(0,1)，也就是渲染区域的左下角对应纹理的左上角，这样一样，渲染出来的图像不是应该倒过来吗？但我们看到的效果却是正确的。
这是因为我们的纹理来自于bitmap，而bitmap的坐标原点是左上角，也就是它和OpenGL中的纹理坐标系是上下颠倒的，所以我们把纹理坐标再颠倒一次，就是正的了。
我们刚才创建纹理时给纹理设置了几个参数，我们来看一下：
```
GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST)
GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST)
GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
```
其中GL_TEXTURE_MIN_FILTER和GL_TEXTURE_MAG_FILTER是纹理过滤参数，指定当我们渲染出来的纹理比原来的纹理小或者大时要如何处理，
这里我们使用了GL_LINEAR的方式，这是一种线性插值的方式，得到的结果会更平滑，除此之外，还有其它很多选项，
还有另一个比较常用的是GL_NEAREST，它会选择和它最近的像素，得到的结果锯齿感比GL_LINEAR要大，我们将顶点坐标扩大5倍，即变成-5~5，来得到放大的效果
GL_TEXTURE_WRAP_S和GL_TEXTURE_WRAP_T则是指定纹理坐标超出了纹理范围之后，该如何填充，比较常用的有GL_CLAMP_TO_EDGE和GL_REPEAT，它们的效果分别是填充边缘像素和重复这个纹理

## FrameBuffer
frame buffer，即帧缓存，顾名思义，它就是能缓存一帧的这么个东西，我们都是通过一次渲染把内容渲染到屏幕（严格来说是渲染到GLSurfaceview上），
如果我们的渲染由多个步骤组成，而每个步骤的渲染结果会给到下一个步骤作为输入，那么就要用到frame buffer，比如说我们今天的例子中的一个效果：
先把图片的蓝色通道全都设置为0.5，得到的结果再去做一个水平方向的模糊，这时渲染过程就由2步组成，第一步的操作不应该显示到屏幕上，应该有个地方存着它的结果，作为第二步的输入，
然后第二步的渲染结果才直接显示到屏幕上。

frame buffer有一些个attachment，例如color attachment、depth attachment、stencil attachment，frame buffer具有什么样的功能，就与frame buffer绑定的attachment有关。
其中color attachment就是用来绑定texture的，当将一个color attachment绑定到一个texture上后，就可以用这个frame buffer来承载渲染的结果，渲染的结果实际上是到了这个绑定的texture上。
depth attachment是用来存储深度信息的，在3D渲染时才会用到，stencil attachment则是在模板测试时会用到。
可以看到，frame buffer本身其实并不会存储数据，都是通过attachment去绑定别的东西来存储相应的数据，
我们今天要讲的就是color attachment，我们会将frame buffer中的一个attachment绑定到一个texture上，然后先将第一步的效果渲染到这个frame buffer上作为中间结果，然后将这个texture作为第二步的输入。
```
// vertex shader
precision mediump float;
attribute vec4 a_position;
attribute vec2 a_textureCoordinate;
varying vec2 v_textureCoordinate;
void main() {
    v_textureCoordinate = a_textureCoordinate;
    gl_Position = a_position;
}

// fragment shader 0
precision mediump float;
varying vec2 v_textureCoordinate;
uniform sampler2D u_texture;
void main() {
    vec4 color = texture2D(u_texture, v_textureCoordinate);
    color.b = 0.5;
    gl_FragColor = color;
}

// fragment shader 1
precision mediump float;
varying vec2 v_textureCoordinate;
uniform sampler2D u_texture;
void main() {
    float offset = 0.005;
    vec4 color = texture2D(u_texture, v_textureCoordinate) * 0.11111;
    color += texture2D(u_texture, vec2(v_textureCoordinate.x - offset, v_textureCoordinate.y)) * 0.11111;
    color += texture2D(u_texture, vec2(v_textureCoordinate.x + offset, v_textureCoordinate.y)) * 0.11111;
    color += texture2D(u_texture, vec2(v_textureCoordinate.x - offset * 2.0, v_textureCoordinate.y)) * 0.11111;
    color += texture2D(u_texture, vec2(v_textureCoordinate.x + offset * 2.0, v_textureCoordinate.y)) * 0.11111;
    color += texture2D(u_texture, vec2(v_textureCoordinate.x - offset * 3.0, v_textureCoordinate.y)) * 0.11111;
    color += texture2D(u_texture, vec2(v_textureCoordinate.x + offset * 3.0, v_textureCoordinate.y)) * 0.11111;
    color += texture2D(u_texture, vec2(v_textureCoordinate.x - offset * 4.0, v_textureCoordinate.y)) * 0.11111;
    color += texture2D(u_texture, vec2(v_textureCoordinate.x + offset * 4.0, v_textureCoordinate.y)) * 0.11111;
    gl_FragColor = color;
}
```
我们要渲染两个效果，这两个效果使用的vertex shader是一样的，主要是fragment shader不同，fragment shader 0将蓝色通道全部设置了0.5，fragment shader 1是做了水平方向的模糊。

我们先创建一个texture作为和frame buffer的color attachment绑定的texture：
```
// 创建frame buffer绑定的纹理
// Create texture which binds to frame buffer
val textures = IntArray(1)
GLES20.glGenTextures(textures.size, textures, 0)
frameBufferTexture = textures[0]
```
接下来创建一个frame buffer，它和创建一个texture非常类似：
```
// 创建frame buffer
// Create frame buffer
val frameBuffers = IntArray(1)
GLES20.glGenFramebuffers(frameBuffers.size, frameBuffers, 0)
frameBuffer = frameBuffers[0]
```
然后将texture与frame buffer的color attachment绑定：
```
// 将frame buffer与texture绑定
// Bind the texture to frame buffer
GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameBufferTexture)
GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null)
GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer)
GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, frameBufferTexture, 0)
GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null)
```
我们先绑定了frameBufferTexture，因此拉下来的操作都会对frameBufferTexture生效，紧接着我们给frameBufferTexture设置了一些参数并分配，
然后和绑定frameBufferTexture类似，要对一个frame buffer进行操作，也需要先将它进行绑定，接下来的glFramebufferTexture2D()就是将frameBufferTexture绑定到frameBuffer的0号attachment上，
即GL_COLOR_ATTACHMENT0，这里大家注意一点，frame buffer有多个color attachment，但在OpenGL ES 2.0中，只能将texture绑定到0号attachment上，以下是官方API说明对attachment参数的描述：
```
attachment
Specifies the attachment point to which an image from texture should be attached. 
Must be one of the following symbolic constants: 
GL_COLOR_ATTACHMENT0, GL_DEPTH_ATTACHMENT, or GL_STENCIL_ATTACHMENT.
```
现在我们已经将frameBufferTexture与frameBuffer进行了绑定，接下来我们要使用它，使用的方法非常简单，就是在渲染前将它绑定即可：
```
override fun onDrawFrame(gl: GL10?) {

    // 绑定第0个GL Program
    // Bind GL program 0
    bindGLProgram(programId0, imageTexture, textureCoordinateDataBuffer0)

    // 绑定frame buffer
    // Bind the frame buffer
    bindFrameBuffer(frameBuffer)

    // 执行渲染，渲染效果为将图片的蓝色通道全部设为0.5
    // Perform rendering, and we can get the result of blue channel set to 0.5
    render()

    // 绑定第1个GL Program
    // Bind GL program 1
    bindGLProgram(programId1, frameBufferTexture, textureCoordinateDataBuffer1)

    // 绑定0号frame buffer
    // Bind the 0# frame buffer
    bindFrameBuffer(0)

    // 执行渲染，渲染效果水平方向的模糊
    // Perform rendering, and we can get the result of horizontal blur base on the previous result
    render()
    
}
private fun bindFrameBuffer(frameBuffer : Int) {

    // 绑定frame buffer
    // Bind the frame buffer
    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer)

}
```
这里注意一点，0号frame buffer是一个特殊的frame buffer，它是默认的frame buffer，即如果我们没有使用glBindFramebuffer()去绑定过frame buffer，
它就是绑定到0号frame buffer上的，0号frame buffer通常代表屏幕，离屏渲染除外，这个暂不讨论，现在大家只需要知道将frame buffer绑定到0就能渲染到屏幕上就行了。

# OpenGL ES 3.0
