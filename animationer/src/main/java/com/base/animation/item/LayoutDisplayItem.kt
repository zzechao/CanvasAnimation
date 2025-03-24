package com.base.animation.item

import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.view.LayoutInflater
import android.view.View
import androidx.core.graphics.withSave
import com.base.animation.DoubleLinkedReference
import com.base.animation.OnAnimItemClick
import com.base.animation.gles.EGLRender
import com.base.animation.model.AnimDrawObject


/**
 * @author:zhouzechao
 * @date: 2020/12/23
 * description：单个view的绘制item
 */
class LayoutDisplayItem(val context: Context, private val layout: Int) : BaseDisplayItem() {

    private val view: View by lazy {
        LayoutInflater.from(context.applicationContext).inflate(layout, null)
    }

    init {
        val widthSpec =
            View.MeasureSpec.makeMeasureSpec(displayWidth, View.MeasureSpec.UNSPECIFIED)
        val heightSpec =
            View.MeasureSpec.makeMeasureSpec(displayHeight, View.MeasureSpec.UNSPECIFIED)
        view.measure(widthSpec, heightSpec)
        displayWidth = view.measuredWidth
        displayHeight = view.measuredHeight
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }

    override fun drawDisplayItem(
        canvas: Canvas,
        x: Float,
        y: Float,
        alpha: Int,
        scaleX: Float,
        scaleY: Float
    ) {
        val drawY = y - (displayHeight / scaleY / 2f)
        canvas.translate(x, drawY)
        view.alpha = alpha.toFloat()
        view.draw(canvas)
    }

    override fun drawDisplayItem(animId: Long, render: EGLRender, x: Float, y: Float, alpha: Int, scaleX: Float, scaleY: Float, rotation: Float) {
        //render.drawAnim(x, y, alpha, scaleX, scaleY, rotation)
    }


    override fun getScalePX(scaleX: Float): Float {
        return displayWidth / 2f
    }

    override fun getScalePY(scaleY: Float): Float {
        return displayHeight / 2f
    }

    override fun touch(animId: Long, onAnimItemClick: OnAnimItemClick, animDrawObject: AnimDrawObject, touchPoint: DoubleLinkedReference<PointF>, extra: String) {
    }
}