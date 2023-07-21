package com.base.animation.cache

import java.lang.annotation.Inherited

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

/**
 * 是否执行对象缓存，对应经常创建的使用
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
annotation class AteDisplayItem(
    val usePoolCache: Boolean = false
)
