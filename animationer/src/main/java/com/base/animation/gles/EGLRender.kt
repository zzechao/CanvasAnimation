package com.base.animation.gles

import android.graphics.SurfaceTexture
import android.opengl.GLES20

/**
 * @author zzechao
 * @date 2025/3/19 18:36
 */
class EGLRender : IRenderer {
    private val mEGLHelper by lazy { EGLHelper() }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        mEGLHelper.initEGL(surface)
        GLES20.glViewport(0, 0, width, height)
        GLES20.glClearColor(0f, 0f, 1f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        mEGLHelper.destroyEGL()
        return false
    }
}