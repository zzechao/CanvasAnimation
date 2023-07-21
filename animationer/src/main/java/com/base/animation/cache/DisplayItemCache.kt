package com.base.animation.cache

import com.base.animation.Animer
import com.base.animation.item.BaseDisplayItem
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

/**
 * @author:zhouzechao
 * @date: 1/22/21
 * description：DisplayItem缓存，策略是10秒没用的自动释放掉
 */

private const val TAG = "DisplayItemCache"

class DisplayItemCache {

    /**
     * 一级缓存为时间超时策略，默认10秒
     */
    private var caches = ConcurrentHashMap<String, BaseDisplayItem>()

    /**
     * 用于判断对应caches的key和clazz是否存在，避免占用caches过多引用导致的多线程问题
     */
    private var cacheKeyClazz = ConcurrentHashMap<String, KClass<out BaseDisplayItem>>()

    /**
     * 二级缓存为对象缓存策略，生命是当前view的生命周期
     */
    private var poolsMap = ConcurrentHashMap<String, DisplayItemPools>()

    fun <T : BaseDisplayItem> putDisplayItems(displayItems: MutableMap<String, T>) {
        Animer.log.i(TAG, "putDisplayItems displayItems:$displayItems")
        displayItems.map {
            if (!caches.containsKey(it.key)) {
                caches[it.key] = it.value
                cacheKeyClazz[it.key] = it.value::class
            }
        }
    }

    /**
     * 获取
     */
    fun getDisplayItem(displayItemId: String): BaseDisplayItem? {
        return caches[displayItemId]
    }

    /**
     * 是否含有对应的key和clazz
     */
    fun hasDisplayItem(key: String, clazz: KClass<out BaseDisplayItem>): Boolean {
        cacheKeyClazz[key]?.let {
            return it == clazz
        }
        return false
    }

    /**
     * 获取二级缓存
     */
    fun <T : BaseDisplayItem> obtain(clazz: KClass<out BaseDisplayItem>): T? {
        val name = clazz.java.canonicalName
        Animer.log.i(TAG, "obtain name:$name poolsMap:${poolsMap.keys}")
        name?.let {
            return (poolsMap[name]?.obtain() as? T).apply {
                Animer.log.i(TAG, "obtain this:$this")
            }
        }
        return null
    }

    /**
     * 清空一级缓存并保存二级缓存,根据@AteDisplayItem注解看看这个class是否启动二级缓存
     */
    fun recycler() {
        caches.mapValues {
            val value = it.value
            val clazzName = it.value::class.java.canonicalName
            value::class.findAnnotation<AteDisplayItem>()?.let {
                if (it.usePoolCache) {
                    Animer.log.i(TAG, "recycler clazzName:$clazzName -- value:$value")
                    clazzName?.let {
                        var pools = poolsMap[clazzName]
                        if (pools == null) {
                            pools = DisplayItemPools(value.poolSize())
                            poolsMap[clazzName] = pools
                        }
                        pools.recycle(value)
                        Animer.log.i(TAG, "recycler poolsSize:${pools.mPools.toMutableList()}")
                    }
                }
            }
        }
        caches.clear()
        cacheKeyClazz.clear()
    }

    /**
     * 清空一级和二级缓存
     */
    fun clear() {
        caches.clear()
        cacheKeyClazz.clear()
        poolsMap.clear()
    }
}