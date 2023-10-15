package com.base.animation.model

import android.graphics.Canvas
import android.graphics.PointF
import com.base.animation.helper.IPathObjectDeal

abstract class BaseAnimDrawObject {

    abstract fun draw(
        canvas: Canvas,
        pathObjectDeal: IPathObjectDeal,
        framePositionCount: Int,
        frameTime: Long,
        touchPoint: MutableList<PointF>? = null
    )
}