package com.base.animation.gles

import android.graphics.SurfaceTexture
import android.opengl.GLES20
import com.base.animation.gles.test.Image


/**
 * @author zzechao
 * @date 2025/3/19 18:36
 */
class EGLRender : IRenderer {

    private val mEGLHelper by lazy { EGLHelper() }
    private val image = Image()
    override var mSurface: SurfaceTexture? = null

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        mSurface = surface
        mEGLHelper.initEGL(surface)
        GLES20.glViewport(0, 0, width, height)
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        image.surfaceCreated()
        image.surfaceChanged(width, height)
        mEGLHelper.swapBuffers()
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        image.surfaceChanged(width, height)
        mEGLHelper.swapBuffers();
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        mEGLHelper.destroyEGL()
        return false
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
        mSurface = surface
    }

    fun drawAnim(
        textureID: Int, displayWidth: Int, displayHeight: Int, x: Float,
        y: Float, alpha: Int, scaleX: Float, scaleY: Float, rotation: Float
    ) {
        image.draw(textureID, displayWidth, displayHeight, x, y, alpha, scaleX, scaleY, rotation)
        mEGLHelper.swapBuffers()
    }
}