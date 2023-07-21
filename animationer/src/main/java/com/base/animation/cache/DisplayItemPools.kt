package com.base.animation.cache

import com.base.animation.Animer
import com.base.animation.item.BaseDisplayItem

/**
 * @author:zhouzechao
 * @date: 1/24/21
 * description：DisplayItems 的二级缓存,对象缓存
 */
private const val TAG = "DisplayItemPools"

class DisplayItemPools(poolSize: Int = 20) {
    var mPools: Array<IRecycle?> = arrayOfNulls<IRecycle?>(poolSize)

    @Volatile
    private var mPoolSize = 0

    /**
     * 少了执行队列的模块
     */
    fun <T : BaseDisplayItem> obtain(): T? {
        return acquire()
    }

    private fun <T : BaseDisplayItem> acquire(): T? {
        return if (mPoolSize > 0) {
            Animer.log.i(TAG, "acquire mPoolSize:$mPoolSize")
            val lastPooledIndex = mPoolSize - 1
            val instance: BaseDisplayItem? = mPools[lastPooledIndex] as? BaseDisplayItem
            mPools[lastPooledIndex] = null
            --mPoolSize
            instance as? T
        } else {
            null
        }
    }

    fun <T : BaseDisplayItem> recycle(clazzInit: T): Boolean {
        clazzInit.recycle()
        return release(clazzInit)
    }

    private fun release(instance: BaseDisplayItem): Boolean {
        return when {
            isInPool(instance) -> {
                Animer.log.i(TAG, "release isInPool")
                false
            }
            mPoolSize < mPools.size -> {
                Animer.log.i(TAG, "release mPoolSize:$mPoolSize")
                mPools[mPoolSize] = instance
                ++mPoolSize
                true
            }
            else -> {
                Animer.log.i(TAG, "release")
                false
            }
        }
    }

    private fun isInPool(instance: BaseDisplayItem): Boolean {
        for (i in 0 until mPoolSize) {
            if (mPools[i] === instance) {
                return true
            }
        }
        return false
    }
}