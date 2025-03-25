package com.base.animation.gles.test

import android.opengl.GLES20
import android.opengl.Matrix
import android.util.Log
import com.base.animation.gles.utils.GLESUtils.loadShader
import com.base.animation.gles.utils.MatrixUtils.flip
import com.base.animation.gles.utils.rotate
import com.base.animation.gles.utils.scale
import com.base.animation.gles.utils.translate
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * 定义图片
 */
class Image {
    private var mDisplayScaleX: Float = 1.0f
    private var mDisplayScaleY: Float = 1.0f

    /**
     * 绘制的流程
     * 1.顶点着色程序 - 用于渲染形状的顶点的 OpenGL ES 图形代码
     * 2.片段着色器 - 用于渲染具有特定颜色或形状的形状的 OpenGL ES 代码纹理。
     * 3.程序 - 包含您想要用于绘制的着色器的 OpenGL ES 对象 一个或多个形状
     *
     *
     * 您至少需要一个顶点着色器来绘制形状，以及一个 fragment 着色器来为该形状着色。
     * 这些着色器必须经过编译，然后添加到 OpenGL ES 程序中，该程序随后用于绘制形状。
     */
    // 顶点着色器代码
    private val vertexShaderCode = """uniform mat4 uMVPMatrix;
        attribute vec4 vPosition;
        attribute vec2 vTexCoordinate;
        varying vec2 aTexCoordinate;
        void main() {
          gl_Position = uMVPMatrix * vPosition;
          aTexCoordinate = vTexCoordinate;
        }
        """

    // 片段着色器代码
    private val fragmentShaderCode = """precision mediump float;
        uniform sampler2D vTexture;
        varying vec2 aTexCoordinate;
        void main() {
          gl_FragColor = texture2D(vTexture, aTexCoordinate);
        }
        """

    private var mProgram = 0

    // 顶点坐标缓冲区
    private val vertexBuffer: FloatBuffer

    // 纹理坐标缓冲区
    private val textureBuffer: FloatBuffer

    /**
     * 顶点坐标数组
     * 顶点坐标系中原点(0,0)在画布中心
     * 向左为x轴正方向
     * 向上为y轴正方向
     * 画布四个角坐标如下：
     * (-1, 1),(1, 1)
     * (-1,-1),(1,-1)
     */
    private val vertexCoords = floatArrayOf(
        -1.0f, 1.0f,  // 左上
        -1.0f, -1.0f,  // 左下
        1.0f, 1.0f,  // 右上
        1.0f, -1.0f,  // 右下
    )

    /**
     * 纹理坐标数组
     * 这里我们需要注意纹理坐标系，原点(0,0s)在画布左下角
     * 向左为x轴正方向
     * 向上为y轴正方向
     * 画布四个角坐标如下：
     * (0,1),(1,1)
     * (0,0),(1,0)
     */
    private val textureCoords = floatArrayOf(
        0.0f, 1.0f,  // 左上
        0.0f, 0.0f,  // 左下
        1.0f, 1.0f,  // 右上
        1.0f, 0.0f,  // 右下
    )

    private var positionHandle = 0

    // 纹理坐标句柄
    private var texCoordinateHandle = 0

    // 纹理Texture句柄
    private var texHandle = 0

    // Use to access and set the view transformation
    private var vPMatrixHandle = 0

    private val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    private var surfaceWidth = 0f
    private var surfaceHeight = 0f

    init {
        // 初始化形状坐标的顶点字节缓冲区
        vertexBuffer = ByteBuffer.allocateDirect(vertexCoords.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertexCoords)
        vertexBuffer.position(0)

        // 初始化纹理坐标顶点字节缓冲区
        textureBuffer = ByteBuffer.allocateDirect(textureCoords.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(textureCoords)
        textureBuffer.position(0)
    }

    fun surfaceCreated() {
        // 加载顶点着色器程序
        val vertexShader = loadShader(
            GLES20.GL_VERTEX_SHADER, vertexShaderCode
        )
        // 加载片段着色器程序
        val fragmentShader = loadShader(
            GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode
        )

        // 创建空的OpenGL ES程序
        mProgram = GLES20.glCreateProgram()
        // 将顶点着色器添加到程序中
        GLES20.glAttachShader(mProgram, vertexShader)
        // 将片段着色器添加到程序中
        GLES20.glAttachShader(mProgram, fragmentShader)
        // 创建OpenGL ES程序可执行文件
        GLES20.glLinkProgram(mProgram)

        // 获取顶点着色器vPosition成员的句柄
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
        // 获取顶点着色器中纹理坐标的句柄
        texCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "vTexCoordinate")
        // 获取绘制矩阵句柄
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
        // 获取Texture句柄
        texHandle = GLES20.glGetUniformLocation(mProgram, "vTexture")

        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            GLES20.glDeleteProgram(mProgram)
            mProgram = 0
        }
    }

    fun surfaceChanged(width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        surfaceWidth = width.toFloat()
        surfaceHeight = height.toFloat()
    }

    fun draw(
        textureID: Int, displayWidth: Int, displayHeight: Int, x: Float, y: Float, alpha: Int, scaleX: Float, scaleY: Float, rotation: Float
    ) {
        Log.d(TAG, "draw: $textureID $displayWidth $displayHeight $x $y $alpha $scaleX $scaleY $rotation ")

        // 为正方形顶点启用控制句柄
        GLES20.glEnableVertexAttribArray(positionHandle)
        // 写入坐标数据
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer)

        // 启用纹理坐标控制句柄
        GLES20.glEnableVertexAttribArray(texCoordinateHandle)
        // 写入坐标数据
        GLES20.glVertexAttribPointer(texCoordinateHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, textureBuffer)

        mDisplayScaleX = surfaceWidth / (displayWidth / 2f)
        mDisplayScaleY = surfaceHeight / (displayHeight / 2f)

        val mMVPMatrix = FloatArray(16)
        getMatrix(mMVPMatrix)
        /**
         * 由于Bitmap拷贝到纹理中，数据从Bitmap左上角开始拷贝到纹理的原点(0,0)
         * 导致图像上下翻转了180度，所以绘制坐标需要上下翻转180度才行
         */
        flip(mMVPMatrix, false, true)

        val drawX = (x - displayWidth / 4f) / surfaceWidth * mDisplayScaleX
        val drawY = (y - displayHeight / 4f) / surfaceHeight * mDisplayScaleY

        // 将投影和视图变换传递给着色器
        GLES20.glUniformMatrix4fv(
            vPMatrixHandle, 1, false, mMVPMatrix.translate(drawX, drawY).rotate(rotation).scale(scaleX, scaleY), 0
        )

        // 激活纹理编号0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        // 绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID)
        // 设置纹理采样器编号，该编号和glActiveTexture中设置的编号相同
        GLES20.glUniform1i(texHandle, 0)

        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFuncSeparate(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        // 绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        // 禁用顶点阵列
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(texCoordinateHandle)
        GLES20.glDisableVertexAttribArray(texHandle)
    }


    private fun getMatrix(
        matrix: FloatArray?
    ) {
        val projection = FloatArray(16)
        val camera = FloatArray(16)
        //Log.i(TAG, "getMatrix imgWidth:$imgWidth imgHeight:$imgHeight viewWidth:$viewWidth viewHeight:$viewHeight scaleX:$scaleX scaleY:$scaleY --${sWhImg / sWhView}")
        Matrix.orthoM(projection, 0, -1f, 1f * mDisplayScaleX, -1f * mDisplayScaleY, 1f, 1f, -1f)
        Matrix.setLookAtM(camera, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f)
        Matrix.multiplyMM(matrix, 0, projection, 0, camera, 0)
    }

    fun glClearCreate() {
        // 将程序添加到OpenGL ES环境
        GLES20.glUseProgram(mProgram)
    }

    fun surfaceDestroyed() {
        GLES20.glDeleteProgram(mProgram)
    }

    companion object {
        private val TAG: String = Image::class.java.simpleName

        // 此数组中每个顶点的坐标数
        const val COORDS_PER_VERTEX: Int = 2
    }
}
