package com.base.animation.model

import android.graphics.Canvas
import android.graphics.PointF
import android.util.Log
import com.base.animation.helper.PathObjectDeal
import com.base.animation.item.BaseDisplayItem

/**
 * @author:zhouzechao
 * @date: 2020/12/9
 * description：绘制节点
 */
private const val TAG = "DrawObject"

class DrawObject(val animId: Long) {

    var animDraws: MutableMap<Int, MutableList<AnimDrawObject>> = mutableMapOf()

    private var currencyPosition: Int = 0

    var status: Status = Status.INIT

    fun draw(
        canvas: Canvas,
        pathObjectDeal: PathObjectDeal,
        framePositionCount: Int,
        touchPoint: MutableList<PointF>? = null
    ) {
        if (currencyPosition >= animDraws.size - 1) {
            currencyPosition = animDraws.size - 1
            status = Status.STOP
            pathObjectDeal.animListeners.forEach {
                it?.endAnim(animId)
            }
            pathObjectDeal.removeAnimId(animId)
            return
        } else {
            if (currencyPosition == 0) {
                status = Status.START
                pathObjectDeal.animListeners.forEach {
                    it?.startAnim(animId)
                }
            } else if (status == Status.START) {
                status = Status.DRAWING
                pathObjectDeal.animListeners.forEach {
                    it?.runningAnim(animId)
                }
            }
            var curDisplayItemId = ""
            var displayItem: BaseDisplayItem? = null
            animDraws[currencyPosition]?.forEach { drawObject ->
                if (drawObject.displayItemId != curDisplayItemId || displayItem == null) {
                    curDisplayItemId = drawObject.displayItemId
                    displayItem = pathObjectDeal.getDisplayItem(drawObject.displayItemId)
                    displayItem?.apply {
                        draw(
                            canvas, drawObject.point.x, drawObject.point.y, drawObject.alpha,
                            drawObject.scaleX, drawObject.scaleY, drawObject.rotation
                        )
                        if (drawObject.clickable && !touchPoint.isNullOrEmpty()
                            && pathObjectDeal.clickIntercepts.isNotEmpty()
                        ) {
                            touch(animId, pathObjectDeal.clickIntercepts, drawObject, touchPoint)
                        }
                    }
                } else {
                    displayItem?.apply {
                        draw(
                            canvas, drawObject.point.x, drawObject.point.y, drawObject.alpha,
                            drawObject.scaleX, drawObject.scaleY, drawObject.rotation
                        )
                        if (drawObject.clickable && !touchPoint.isNullOrEmpty()
                            && pathObjectDeal.clickIntercepts.isNotEmpty()
                        ) {
                            touch(animId, pathObjectDeal.clickIntercepts, drawObject, touchPoint)
                        }
                    }
                }
            }
            currencyPosition += framePositionCount
        }
    }
}

data class AnimDrawObject(
    var displayItemId: String,
    var point: PointF = PointF(0f, 0f),
    var alpha: Int = 100,
    var scaleX: Float = 1.0f,
    var scaleY: Float = 1.0f,
    var rotation: Float = 0.0f,
    var clickable: Boolean,
    var expand: String
)

/**
 * 转化
 */
fun PathObject.toAnimDrawObject(clickable: Boolean, expand: String): AnimDrawObject {
    return AnimDrawObject(
        displayItemId,
        point,
        alpha,
        scaleX,
        scaleY,
        rotation,
        clickable = clickable,
        expand = expand
    )
}

enum class Status(val value: Int) {
    INIT(0),
    START(1),
    DRAWING(2),
    STOP(3)
}