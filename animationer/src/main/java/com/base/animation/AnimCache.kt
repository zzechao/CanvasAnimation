package com.base.animation

import android.graphics.Bitmap
import com.base.animation.cache.DisplayItemCache

object AnimCache {
    /**
     * 画布缓存
     */
    val displayItemCache = DisplayItemCache()


    fun putBitmapCache(key: String, bitmap: Bitmap) {
        displayItemCache.bitmapCache.put(key, bitmap)
    }

    fun getBitmapCache(key: String): Bitmap? {
        return displayItemCache.bitmapCache.getIfPresent(key)
    }
}