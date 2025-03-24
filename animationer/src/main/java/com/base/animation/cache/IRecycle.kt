package com.base.animation.cache

/**
 * @author:zhouzechao
 * @date: 1/24/21
 * description：二级缓存的回收和重塑
 */
interface IRecycle {

    fun poolSize(): Int

    fun reInit()

    fun recycle()
}