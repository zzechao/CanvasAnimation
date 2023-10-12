package com.base.animation.item

import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.graphics.withSave
import com.base.animation.IClickIntercept
import com.base.animation.model.AnimDrawObject
import kotlin.math.acos
import kotlin.math.acosh
import kotlin.math.asin
import kotlin.math.asinh
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.cosh
import kotlin.math.round
import kotlin.math.tanh

/**
 * @author:zhouzechao
 * @date: 2020/12/23
 * description：单个view的绘制item
 */
class LayoutDisplayItem(val context: Context, private val layout: Int) : BaseDisplayItem() {

    private val view: View by lazy {
        LayoutInflater.from(context.applicationContext).inflate(layout, null)
    }

    private var isMeasure: Boolean = false

    override fun draw(
        canvas: Canvas,
        x: Float,
        y: Float,
        alpha: Int,
        scaleX: Float,
        scaleY: Float,
        rotation: Float
    ) {
        if (!isMeasure) {
            isMeasure = true
            val widthSpec =
                View.MeasureSpec.makeMeasureSpec(displayWidth, View.MeasureSpec.UNSPECIFIED)
            val heightSpec =
                View.MeasureSpec.makeMeasureSpec(displayHeight, View.MeasureSpec.UNSPECIFIED)
            view.measure(widthSpec, heightSpec)
            displayWidth = view.measuredWidth
            displayHeight = view.measuredHeight
            view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        }
        canvas.withSave {
            var drawX = x
            var drawY = y
            if (scaleX != 1f || scaleY != 1f) {
                drawX = x - (displayWidth / scaleX / 2f)
                drawY = y - (displayHeight / scaleY / 2f)
                canvas.scale(scaleX, scaleY, x + getScalePX(scaleX), y + getScalePY(scaleY))
            }
            if (rotation != 0f) {
                Log.i(
                    "zzc3",
                    "rotation ${drawX + getRotatePX(rotation, scaleX)}--${
                        drawY + getRotatePY(
                            rotation,
                            scaleY
                        )
                    }-${cos(round(rotation))}-${round(rotation)}"
                )
                canvas.rotate(
                    rotation, drawX + getRotatePX(rotation, scaleX),
                    drawY + getRotatePY(rotation, scaleY)
                )
            }
            draw(canvas, x, y, alpha, scaleX, scaleY)
        }
    }

    override fun draw(
        canvas: Canvas,
        x: Float,
        y: Float,
        alpha: Int,
        scaleX: Float,
        scaleY: Float
    ) {
        val drawX = x - (displayWidth / scaleX / 2f)
        val drawY = y - (displayHeight / scaleY / 2f)
        canvas.translate(drawX, drawY)
        view.alpha = alpha.toFloat()
        view.draw(canvas)
    }


    override fun getScalePX(scaleX: Float): Float {
        return displayWidth * 1f / 2
    }

    override fun getScalePY(scaleY: Float): Float {
        return displayHeight * 1f / 2
    }

    override fun getRotatePX(rotation: Float, scaleX: Float): Float {
        return displayWidth   / 2f
    }

    override fun getRotatePY(rotation: Float, scaleY: Float): Float {
        return displayHeight  / 2f
    }

    override fun touch(
        animId: Long,
        iClickIntercepts: MutableList<IClickIntercept>,
        animDrawObject: AnimDrawObject,
        touchPoint: MutableList<PointF>
    ) {
    }
}