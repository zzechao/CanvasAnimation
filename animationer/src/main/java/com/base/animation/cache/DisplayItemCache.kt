package com.base.animation.cache

import com.base.animation.Animer
import com.base.animation.item.BaseDisplayItem
import com.google.common.cache.CacheBuilder
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

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
            .maximumSize(10)
            .initialCapacity(5)
            .expireAfterAccess(5, TimeUnit.SECONDS)
            .removalListener<String, BaseDisplayItem> {}
            .build()


    fun <T : BaseDisplayItem> putDisplayItems(displayItems: MutableMap<String, T>) {
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
    fun hasDisplayItem(key: String, clazz: KClass<out BaseDisplayItem>): Boolean {
        return caches.getIfPresent(key) != null
    }

    /**
     * 清空一级和二级缓存
     */
    fun clear() {
        caches.invalidateAll()
    }
}