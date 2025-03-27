package com.base.animation.gles

import android.content.Context
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.TextureView
import android.view.View
import com.base.animation.*

/**
 * @author zzechao
 * @date 2025/3/19 15:46
 * @desc EGL动画View
 *  继承TextureView，实现TextureView.SurfaceTextureListener，实现IAnimView接口
 *  实现IAnimView接口的方法，通过EGLAnimPlayer来实现
 */
class EGLAnimView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, private val player: EGLAnimPlayer = EGLAnimPlayer()
) : TextureView(context, attrs), TextureView.SurfaceTextureListener, IAnimView by player {

    init {
        isFocusable = true
        isOpaque = false
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

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
        player.onSurfaceTextureUpdated(surface)
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

    override fun onDetachedFromWindow() {
        player.onDetachedFromWindow()
        super.onDetachedFromWindow()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        player.onAttachedToWindow()
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) touchAnimEvent(event)
        return super.dispatchTouchEvent(event)
    }
}