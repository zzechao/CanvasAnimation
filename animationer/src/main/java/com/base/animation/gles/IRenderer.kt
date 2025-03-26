package com.base.animation.gles

import android.graphics.SurfaceTexture

/**
 * @author zzechao
 * @date 2025/3/20 20:00
 */
interface IRenderer {

    fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int)

    fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int)

    fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean

    fun onSurfaceTextureUpdated(surface: SurfaceTexture)
}