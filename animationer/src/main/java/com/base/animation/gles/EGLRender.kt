package com.base.animation.gles

import android.graphics.SurfaceTexture
import android.opengl.GLES20
import com.base.animation.gles.test.Circle
import com.base.animation.gles.test.Square
import com.base.animation.gles.test.Triangle
import kotlin.random.Random


/**
 * @author zzechao
 * @date 2025/3/19 18:36
 */
class EGLRender : IRenderer {

    private val mEGLHelper by lazy { EGLHelper() }
    private val triangle = Triangle()
    private val square = Square()
    private val circle = Circle()

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        mEGLHelper.initEGL(surface)
        GLES20.glViewport(0, 0, width, height)
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        triangle.surfaceCreated()
        triangle.surfaceChanged(width, height)
        square.surfaceCreated()
        square.surfaceChanged(width, height)
        circle.surfaceCreated()
        circle.surfaceChanged(width, height)
        mEGLHelper.swapBuffers()
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        triangle.surfaceChanged(width, height)
        square.surfaceChanged(width, height)
        circle.surfaceChanged(width, height)
        mEGLHelper.swapBuffers();
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        mEGLHelper.destroyEGL()
        return false
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
    }

    fun drawAnim(framePositionCount: Int, frameTime: Long) {
        when (Random.nextInt(3)) {
            0 -> {
                square.draw()
            }

            1 -> {
                circle.draw()
            }

            2 -> {
                triangle.draw()
            }
        }
        mEGLHelper.swapBuffers()
    }
}