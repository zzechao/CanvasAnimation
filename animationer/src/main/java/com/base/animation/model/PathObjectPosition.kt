package com.base.animation.model

import android.graphics.PointF
import android.view.animation.BaseInterpolator
import android.view.animation.LinearInterpolator

/**
 * @author:zhouzechao
 * @date: 1/26/21
 * description：位置节点信息，用于区分开始和下个节点的匹配问题
 */
class PathObjectPosition {

    companion object {
        fun with(): PathObjectPosition {
            return PathObjectPosition()
        }
    }

    var position = 0

    fun createPathObject(
        displayItemId: String,
        point: PointF = PointF(0f, 0f),
        alpha: Int = 255,
        scaleX: Float = 1.0f,
        scaleY: Float = 1.0f,
        rotation: Float = 0.0f,
        interpolator: BaseInterpolator = LinearInterpolator()
    ): PathObject {
        val p = position++
        return PathObject(
            displayItemId, point, alpha, scaleX, scaleY, rotation,
            interpolator, p
        )
    }

    fun createPathObject(
        parentPointPosition: Int,
        displayItemId: String,
        point: PointF = PointF(0f, 0f),
        alpha: Int = 255,
        scaleX: Float = 1.0f,
        scaleY: Float = 1.0f,
        rotation: Float = 0.0f,
        interpolator: BaseInterpolator = LinearInterpolator()
    ): PathObject {
        val p = position++
        return PathObject(
            displayItemId, point, alpha, scaleX, scaleY, rotation,
            interpolator, p
        ).apply {
            this.parentPointPosition = parentPointPosition
        }
    }
}