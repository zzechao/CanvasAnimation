package com.base.animation.gles


import android.opengl.GLES20
import android.util.Log
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.cache.RemovalListener
import java.util.concurrent.TimeUnit

/**
 * @author zzechao
 * 纹理ID缓存池
 * @date 2025/3/24 19:02
 */
class EGLTexturePools {
    companion object {
        private const val DISPLAYMAXCACHESIZE = 50L
    }

    private val textureCaches: Cache<Int, Int> by lazy {
        CacheBuilder.newBuilder().concurrencyLevel(4).maximumSize(DISPLAYMAXCACHESIZE).initialCapacity(10).expireAfterAccess(2, TimeUnit.SECONDS).removalListener(RemovalListener<Int, Int> {
            Log.e("EGLTexturePools", "EGLTexturePools:${it.key} ${it.value}")
            GLES20.glDeleteTextures(1, intArrayOf(it.value), 0)
        }).build()
    }


    fun getTexture(bitmapHash: Int, createTexture: () -> Int): Int {
        return textureCaches.getIfPresent(bitmapHash) ?: createTexture().also {
            putTexture(bitmapHash, it)
        }
    }

    private fun putTexture(bitmapHash: Int, textureId: Int) {
        textureCaches.put(bitmapHash, textureId)
    }
}