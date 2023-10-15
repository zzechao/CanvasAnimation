package com.base.animation.model

import android.graphics.Canvas
import android.graphics.PointF
import com.base.animation.helper.IPathObjectDeal
import com.base.animation.helper.data.PathProcess
import com.base.animation.item.BaseDisplayItem

/**
 * @author:zhouzechao
 * @date: 2020/12/9
 * description：绘制节点
 */
private const val TAG = "DrawObject"

class DrawObject2(val animId: Long) : BaseAnimDrawObject() {

    var animDraws: MutableMap<Int, List<PathProcess>> = mutableMapOf()

    private var currencyPosition: Int = 0

    var status: Status = Status.INIT

    override fun draw(
        canvas: Canvas,
        pathObjectDeal: IPathObjectDeal,
        framePositionCount: Int,
        frameTime: Long,
        touchPoint: MutableList<PointF>?
    ) {
        var curDisplayItemId = ""
        var displayItem: BaseDisplayItem? = null
        var updateCurrencyPosition = true
        animDraws[currencyPosition]?.forEach { drawObject ->
            if (drawObject.start.displayItemId != curDisplayItemId || displayItem == null) {
                curDisplayItemId = drawObject.start.displayItemId
                displayItem = pathObjectDeal.getDisplayItem(drawObject.start.displayItemId)
                displayItem?.apply {
                    drawObject.curTotalTime += frameTime
                    val p = drawObject.curTotalTime / drawObject.durTime
                    val interP = drawObject.interpolator.getInterpolation(p)
                    val inPoint = PointF(
                        drawObject.start.point.x + drawObject.item.totalX * interP,
                        drawObject.start.point.y + drawObject.item.totalY * interP
                    )
                    val alpha =
                        drawObject.start.alpha + (drawObject.item.totalAlpha * interP).toInt()
                    val scaleX =
                        drawObject.start.scaleX + drawObject.item.totalScaleX * interP
                    val scaleY =
                        drawObject.start.scaleY + drawObject.item.totalScaleY * interP
                    val rotation =
                        drawObject.start.rotation + drawObject.item.totalRotation * interP
                    draw(
                        canvas, inPoint.x, inPoint.y, alpha,
                        scaleX, scaleY, rotation
                    )
                }
            } else {
                displayItem?.apply {
                    drawObject.curTotalTime += frameTime
                    val p = drawObject.curTotalTime / drawObject.durTime
                    val interP = drawObject.interpolator.getInterpolation(p)
                    val inPoint = PointF(
                        drawObject.start.point.x + drawObject.item.totalX * interP,
                        drawObject.start.point.y + drawObject.item.totalY * interP
                    )
                    val alpha =
                        drawObject.start.alpha + (drawObject.item.totalAlpha * interP).toInt()
                    val scaleX =
                        drawObject.start.scaleX + drawObject.item.totalScaleX * interP
                    val scaleY =
                        drawObject.start.scaleY + drawObject.item.totalScaleY * interP
                    val rotation =
                        drawObject.start.rotation + drawObject.item.totalRotation * interP
                    draw(
                        canvas, inPoint.x, inPoint.y, alpha,
                        scaleX, scaleY, rotation
                    )
                }
            }
            if (drawObject.curTotalTime < drawObject.durTime) {
                updateCurrencyPosition = false
            }
        }?.apply {
            if (updateCurrencyPosition) {
                currencyPosition += 1
            }
        } ?: kotlin.run {
            pathObjectDeal.removeAnimId(animId)
        }
    }
}