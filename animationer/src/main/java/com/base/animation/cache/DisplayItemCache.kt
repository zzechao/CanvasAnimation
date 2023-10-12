package com.base.animation.cache

import android.graphics.Bitmap
import com.base.animation.Animer
import com.base.animation.item.BaseDisplayItem
import com.google.common.cache.CacheBuilder
import java.util.concurrent.TimeUnit

/**
 * @author:zhouzechao
 * @date: 1/22/21
 * description：DisplayItem缓存，策略是10秒没用的自动释放掉
 */

private const val TAG = "DisplayItemCache"

class DisplayItemCache {

    private val caches: com.google.common.cache.Cache<String, BaseDisplayItem> =
        CacheBuilder.newBuilder()
            .concurrencyLevel(4)
            .maximumSize(50)
            .initialCapacity(5)
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .expireAfterAccess(60, TimeUnit.SECONDS)
            .build()

    val bitmapCache: com.google.common.cache.Cache<String, Bitmap> =
        CacheBuilder.newBuilder().concurrencyLevel(4)
            .maximumSize(20)
            .initialCapacity(5)
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .expireAfterAccess(10, TimeUnit.SECONDS)
            .build()


    fun putDisplayItems(displayItems: MutableMap<String, out BaseDisplayItem>) {
        Animer.log.i(TAG, "putDisplayItems displayItems:${displayItems.size}")
        displayItems.map {
            caches.put(it.key, it.value)
        }
    }

    /**
     * 获取
     */
    fun getDisplayItem(displayItemId: String): BaseDisplayItem? {
        return caches.getIfPresent(displayItemId)
    }

    /**
     * 是否含有对应的key和clazz
     */
    fun hasDisplayItem(key: String): Boolean {
        return caches.getIfPresent(key) != null
    }

    /**
     * 清空一级和二级缓存
     */
    fun clear() {
        caches.invalidateAll()
    }
}