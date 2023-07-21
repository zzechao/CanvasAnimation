package com.base.animation

import android.graphics.PointF
import com.base.animation.model.AnimDrawObject

/**
 * @author:zhouzechao
 * @date: 2/18/21
 * description：点击拦截器
 */
interface IClickIntercept {
    fun intercept(animId: Long, animDrawObject: AnimDrawObject, touchPointF: PointF)
}