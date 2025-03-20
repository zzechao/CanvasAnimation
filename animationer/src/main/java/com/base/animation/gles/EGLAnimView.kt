package com.base.animation.gles

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.os.Build
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
    context: Context, attrs: AttributeSet? = null, private val player: EGLAnimPlayer = EGLAnimPlayer()
) : TextureView(context, attrs), TextureView.SurfaceTextureListener, IAnimView by player {



    init {
        surfaceTextureListener = this
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        player.onSurfaceTextureAvailable(surface, width, height)
    }


    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        player.onSurfaceTextureSizeChanged(surface, width, height)
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        return player.onSurfaceTextureDestroyed(surface)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !surfaceTexture.isReleased) {
            surfaceTexture.release()
        } else {
            kotlin.runCatching { surfaceTexture.release() }
        }
        endAnimation()
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
    }

    override fun getView(): View {
        return this
    }

    override fun getViewByAnimName(name: String): View? {
        return tryCatch {
            val context = AnimationEx.mApplication ?: return@tryCatch null
            val id = context.resources.getIdentifier(name, "id", context.packageName)
            this.findFragmentOfGivenView()?.let { it.view?.findViewById<View>(id) } ?: this.getFragmentActivity()?.findViewById(id)
        }
    }
}