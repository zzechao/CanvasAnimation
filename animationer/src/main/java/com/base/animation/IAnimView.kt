package com.base.animation

import android.view.View
import com.base.animation.model.AnimPathObject

/**
 * @author:zhouzechao
 * @date: 1/22/21
 * description：canvas view的接口类，提供各种信息
 */
interface IAnimView {

    fun resume()

    fun pause()

    fun endAnimation()

    fun addAnimDisplay(animPathObject: AnimPathObject)

    fun removeAnimId(animId: Long)

    fun addAnimListener(iAnimListener: IAnimListener)

    fun removeAnimListener(iAnimListener: IAnimListener?)

    fun addClickIntercept(iClickIntercept: IClickIntercept)

    fun removeClickIntercept(iClickIntercept: IClickIntercept?)

    fun getView(): View? {
        return null
    }

    fun getViewByAnimName(name: String): View? {
        return null
    }
}