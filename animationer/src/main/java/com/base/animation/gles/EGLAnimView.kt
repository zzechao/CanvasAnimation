package com.base.animation.gles

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.util.AttributeSet
import android.view.TextureView
import android.view.View
import com.base.animation.*
import com.base.animation.common.AnimPlayer

/**
 * @author zzechao
 * @date 2025/3/19 15:46
 */
class EGLAnimView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextureView(context, attrs), TextureView.SurfaceTextureListener, IAnimView by AnimPlayer(false) {



    init {
        surfaceTextureListener = this
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        recreate(surface)
        GLES20.glViewport(0, 0, width, height)
        GLES20.glClearColor(0f, 0f, 1f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
    }


    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        GLES20.glClearColor(0f, 0f, 1f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
    }

    private fun recreate(surface: SurfaceTexture) {
    }

    override fun getViewByAnimName(name: String): View? {
        return tryCatch {
            val context = AnimationEx.mApplication ?: return@tryCatch null
            val id = context.resources.getIdentifier(name, "id", context.packageName)
            this.findFragmentOfGivenView()?.let { it.view?.findViewById<View>(id) } ?: this.getFragmentActivity()?.findViewById(id)
        }
    }
}