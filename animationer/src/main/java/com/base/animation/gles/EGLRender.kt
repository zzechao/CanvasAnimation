package com.base.animation.gles

import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.Matrix
import android.util.Log
import com.base.animation.gles.utils.MatrixUtils.flip
import com.base.animation.gles.utils.flip
import com.base.animation.gles.utils.rotate
import com.base.animation.gles.utils.scale
import com.base.animation.gles.utils.translate
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


/**
 * @author zzechao
 * @date 2025/3/19 18:36
 */
class EGLRender : IRenderer {
    companion object {
        private const val TAG = "EGLRender"
        private const val COORDS_PER_VERTEX: Int = 2
    }


    private val mEGLHelper by lazy { EGLHelper() }
    private val shader by lazy { EGLAnimShader() }
    override var mSurface: SurfaceTexture? = null

    private var surfaceWidth = -1
    private var surfaceHeight = -1

    /**
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

    // 顶点坐标缓冲区
    private var vertexBuffer: FloatBuffer? = null

    // 纹理坐标缓冲区
    private var textureBuffer: FloatBuffer? = null

    private val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    private var mDisplayScaleX: Float = -1.0f
    private var mDisplayScaleY: Float = -1.0f

    private val mMVPMatrix by lazy { FloatArray(16) }
    private val projection by lazy { FloatArray(16) }
    private val viewMatrix by lazy { FloatArray(16) }

    private val texturePools by lazy { EGLTexturePools() }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        mSurface = surface

        mEGLHelper.initEGL(surface)

        // 初始化形状坐标的顶点字节缓冲区
        vertexBuffer = ByteBuffer.allocateDirect(vertexCoords.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertexCoords)
        vertexBuffer?.position(0)

        // 初始化纹理坐标顶点字节缓冲区
        textureBuffer = ByteBuffer.allocateDirect(textureCoords.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(textureCoords)
        textureBuffer?.position(0)

        shader.initShader()

        refreshSurfaceView(width, height)
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        refreshSurfaceView(width, height)
    }


    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {

        return false
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
        mSurface = surface
    }

    private fun refreshSurfaceView(width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        surfaceWidth = width
        surfaceHeight = height
        mEGLHelper.swapBuffers()
    }


    fun drawItem(
        animId: Long, bitmapHashCode: Int, displayWidth: Int, displayHeight: Int, x: Float, y: Float,
        alpha: Int, scaleX: Float, scaleY: Float, rotation: Float, createTexture: () -> Int
    ) {
        val textureID = texturePools.getTexture(bitmapHashCode, createTexture)

        /**
         * 这里因为画布坐标和纹理坐标存在两倍的缩放比，所以需要将画布坐标和纹理坐标进行缩放，所以将displaySize/2f
         */
        mDisplayScaleX = surfaceWidth / (displayWidth / 2f)
        mDisplayScaleY = surfaceHeight / (displayHeight / 2f)

        val drawX = (x - displayWidth / 4f) / surfaceWidth * mDisplayScaleX
        val drawY = (y - displayHeight / 4f) / surfaceHeight * mDisplayScaleY

        // 为正方形顶点启用控制句柄
        GLES20.glEnableVertexAttribArray(shader.positionHandle)
        // 写入坐标数据
        GLES20.glVertexAttribPointer(shader.positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer)

        // 启用纹理坐标控制句柄
        GLES20.glEnableVertexAttribArray(shader.texCoordinateHandle)
        // 写入坐标数据
        GLES20.glVertexAttribPointer(shader.texCoordinateHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, textureBuffer)

        Matrix.orthoM(projection, 0, -1f, 1f * mDisplayScaleX, -1f * mDisplayScaleY, 1f, 1f, -1f)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f)
        Matrix.multiplyMM(mMVPMatrix, 0, projection, 0, viewMatrix, 0)
        GLES20.glUniform1f(shader.uAlphaHandle, alpha / 255f)

        // 将投影和视图变换传递给着色器
        GLES20.glUniformMatrix4fv(
            shader.vPMatrixHandle, 1, false,
            mMVPMatrix.flip(false, y = true).translate(drawX, drawY).rotate(rotation).scale(scaleX, scaleY), 0
        )

        // 激活纹理编号0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        // 绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID)
        // 设置纹理采样器编号，该编号和glActiveTexture中设置的编号相同
        GLES20.glUniform1i(shader.texHandle, 0)

        GLES20.glEnable(GLES20.GL_BLEND)
        if (alpha < 255) {
            GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        } else {
            GLES20.glBlendFuncSeparate(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE)
        }

        // 绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        // 禁用顶点阵列
        GLES20.glDisableVertexAttribArray(shader.positionHandle)
        GLES20.glDisableVertexAttribArray(shader.texCoordinateHandle)
    }

    /**
     * 清除颜色
     */
    fun drawRenderBegin() {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        shader.useShader()
    }

    /**
     * 绑定FBO
     */
    fun drawRenderEnd() {
        shader.unUseShader()
        mEGLHelper.swapBuffers()
    }

    fun release() {
        mEGLHelper.destroyEGL()
        shader.destroyShader()
    }
}