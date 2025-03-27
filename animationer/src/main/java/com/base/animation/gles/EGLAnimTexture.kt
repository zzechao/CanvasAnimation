package com.base.animation.gles

import android.graphics.SurfaceTexture
import android.view.Surface

/**
 * @author zzechao
 * @date 2025/3/27 13:49
 * 纹理缓存对象
 * @param textureId 纹理id
 * @param type 纹理类型
 * @param surface 纹理对应的surface
 * @param surfaceTexture 纹理对应的surfaceTexture
 */
class EGLAnimTexture(var textureId: Int = 0, val type: TextureType = TextureType.NONE) {
    var surface: Surface? = null
    var surfaceTexture: SurfaceTexture? = null

    enum class TextureType { NONE, BITMAP, STRING, LAYOUT }
}