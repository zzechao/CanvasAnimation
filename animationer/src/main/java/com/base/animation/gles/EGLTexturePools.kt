package com.base.animation.gles


import android.opengl.GLES20
import android.util.Log
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.cache.RemovalListener
import java.util.concurrent.TimeUnit

/**
 * @author zzechao
 * @date 2025/3/24 19:02
 * @desc 纹理池
 */
class EGLTexturePools {

    companion object {
        private const val DISPLAYMAXCACHESIZE = 50L
    }

    private var nanoTime = 0L

    private val textureCaches: Cache<Int, EGLAnimTexture> by lazy {
        CacheBuilder.newBuilder().concurrencyLevel(4).maximumSize(DISPLAYMAXCACHESIZE).initialCapacity(10).expireAfterAccess(60, TimeUnit.SECONDS).build()
    }

    fun getTexture(bitmapHash: Int, createTexture: () -> EGLAnimTexture): EGLAnimTexture {
        return textureCaches.getIfPresent(bitmapHash)?.let {
            if (System.currentTimeMillis() - nanoTime > 5000) {
                nanoTime = System.currentTimeMillis()
                Log.i("EGLTexturePools", "getTexture:${it} $bitmapHash size:${textureCaches.size()} ${GLES20.glIsTexture(it.textureId)}")
            }
            if (GLES20.glIsTexture(it.textureId)) {
                it
            } else {
                Log.i("EGLTexturePools", "getTexture glIsTexture ${it} $bitmapHash size:${textureCaches.size()} ${GLES20.glIsTexture(it.textureId)}")
                GLES20.glDeleteTextures(1, intArrayOf(it.textureId), 0)
                it.surface?.release()
                createTexture().apply { putTexture(bitmapHash, this) }
            }
        } ?: createTexture().also {
            putTexture(bitmapHash, it)
        }
    }

    private fun putTexture(bitmapHash: Int, textureId: EGLAnimTexture) {
        textureCaches.put(bitmapHash, textureId)
    }

    fun textureCacheMap() = textureCaches.asMap()

    fun clear() {
        textureCaches.invalidateAll()
    }
}