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
 */
object EGLTexturePools {

    private const val DISPLAYMAXCACHESIZE = 50L

    private val textureCaches: Cache<Long, Int> by lazy {
        CacheBuilder.newBuilder()
            .concurrencyLevel(4)
            .maximumSize(DISPLAYMAXCACHESIZE)
            .initialCapacity(10)
            .expireAfterAccess(2, TimeUnit.SECONDS)
            .removalListener(RemovalListener<Long, Int> {
                GLES20.glDeleteTextures(1, intArrayOf(it.value), 0)
            })
            .build()
    }


    fun getTexture(animId: Long, createTexture: () -> Int): Int {
        return textureCaches.getIfPresent(animId).also {
        } ?: createTexture().also {
            putTexture(animId, it)
        }
    }

    private fun putTexture(animId: Long, textureId: Int) {
        textureCaches.put(animId, textureId)
    }
}