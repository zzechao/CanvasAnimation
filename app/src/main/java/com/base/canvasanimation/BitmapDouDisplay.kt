package com.base.canvasanimation

import android.graphics.Bitmap
import android.graphics.Canvas
import com.base.animation.AnimCache
import com.base.animation.item.BitmapDisplayItem

class BitmapDouDisplay(rocation: Int, val bitmapKey: String) : BitmapDisplayItem() {

    override var mBitmap: Bitmap? = null
        get() = AnimCache.getBitmapCache(bitmapKey)

    private var current = 0
    private var max = 0
    private var i = 1

    init {
        current = rocation
        max = current
    }

    override fun draw(
        canvas: Canvas, x: Float, y: Float, alpha: Int, scaleX: Float, scaleY: Float
    ) {
        val drawX = x - (displayWidth / scaleX / 2f)
        val drawY = y - (displayHeight / scaleY / 2f)
        canvas.rotate(
            current.toFloat(),
            drawX + getRotatePX(current.toFloat(), scaleX),
            drawY + getRotatePY(current.toFloat(), scaleY)
        )
        if (current == max * -1) {
            i = 1
        } else if (current == max) {
            i = -1
        }
        current += i
        super.draw(canvas, x, y, alpha, scaleX, scaleY)
    }
}