package com.base.canvasanimation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import com.base.animation.AnimCache
import com.base.animation.item.BitmapDisplayItem
import kotlin.random.Random

class BitmapDouDisplay(val rocation: Int, val bitmapKey: String) : BitmapDisplayItem() {
    override var mBitmap: Bitmap? = null
        get() = AnimCache.getBitmapCache(bitmapKey).apply {
            bitmapWidth = this?.width ?: 50
            bitmapHeight = this?.height ?: 50
        }

    private var current = 0
    private var i = 1

    init {
        current = if (Random.nextBoolean()) {
            rocation
        } else {
            rocation * -1
        }
    }

    override fun setBitmap(bitmap: Bitmap) {
        AnimCache.putBitmapCache(
            bitmapKey, bitmap
        )
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
        if (current == rocation * i) {
            i = if (i < 0) {
                1
            } else {
                -1
            }
        }
        current += i
        Log.i("zzc", "draw current:$current max:$rocation $i")
        super.draw(canvas, x, y, alpha, scaleX, scaleY)
    }
}