package com.base.animation

import com.base.animation.item.BaseDisplayItem
import com.base.animation.model.AnimPathObject
import kotlin.reflect.KClass

/**
 * @author:zhouzechao
 * @date: 1/22/21
 * description：canvas view的接口类，提供各种信息
 */
interface IAnimView {

    fun setCacheTime(cacheTime: Long)

    fun resume()

    fun pause()

    fun endAnimation()

    fun addAnimDisplay(animPathObject: AnimPathObject)

    fun removeAnimId(animId: Long)

    fun hasDisplayItem(key: String, clazz: KClass<out BaseDisplayItem>): Boolean

    fun addAnimListener(iAnimListener: IAnimListener)

    fun removeAnimListener(iAnimListener: IAnimListener?)

    fun addClickIntercept(iClickIntercept: IClickIntercept)

    fun removeClickIntercept(iClickIntercept: IClickIntercept?)
}