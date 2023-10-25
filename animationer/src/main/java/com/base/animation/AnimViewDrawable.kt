package com.base.animation

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable

/**
 * @author:zhouzechao
 * description：*
 */
class AnimViewDrawable(var drawable: (Canvas) -> Unit) : Drawable() {

    init {
        isFilterBitmap = true
    }

    override fun draw(canvas: Canvas) {
        //canvas.drawColor(Color.TRANSPARENT) // 设置画布的背景为透明
        drawable.invoke(canvas)
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }
}