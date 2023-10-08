package com.base.animation.item

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.base.animation.IClickIntercept
import com.base.animation.model.AnimDrawObject

class StringDisplayItem(
    override var displayHeight: Int,
    val message: String,
    txtColor: Int
) : BaseDisplayItem() {

    private val paint by lazy {
        TextPaint().apply {
            color = txtColor
            style = Paint.Style.FILL
            textSize = displayHeight.toFloat()
        }
    }

    private var myStaticLayout: StaticLayout? = null

    init {
        displayWidth = displayHeight * message.length
    }

    override fun draw(
        canvas: Canvas,
        x: Float,
        y: Float,
        alpha: Int,
        scaleX: Float,
        scaleY: Float,
        rotation: Float
    ) {
        if (myStaticLayout == null) {
            myStaticLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StaticLayout.Builder.obtain(
                    message,
                    0,
                    message.length,
                    paint,
                    minOf(displayWidth, canvas.width)
                ).build()
            } else {
                StaticLayout(
                    message,
                    paint,
                    minOf(displayWidth, canvas.width),
                    Layout.Alignment.ALIGN_NORMAL,
                    1.0f,
                    0.0f,
                    false
                )
            }
        }
        super.draw(canvas, x, y, alpha, scaleX, scaleY, rotation)
    }

    override fun draw(
        canvas: Canvas,
        x: Float,
        y: Float,
        alpha: Int,
        scaleX: Float,
        scaleY: Float
    ) {
        myStaticLayout?.paint?.alpha = alpha
        canvas.translate(x, y)
        myStaticLayout?.draw(canvas)
    }

    override fun getScalePX(scaleX: Float): Float {
        return displayWidth * 1f / 2
    }

    override fun getScalePY(scaleY: Float): Float {
        return displayHeight * 1f / 2
    }

    override fun getRotatePX(rotation: Float, scaleX: Float): Float {
        return displayWidth * 1f / 2
    }

    override fun getRotatePY(rotation: Float, scaleY: Float): Float {
        return displayHeight * 1f / 2
    }

    override fun touch(
        animId: Long,
        iClickIntercepts: MutableList<IClickIntercept>,
        animDrawObject: AnimDrawObject,
        touchPoint: MutableList<PointF>
    ) {
    }
}