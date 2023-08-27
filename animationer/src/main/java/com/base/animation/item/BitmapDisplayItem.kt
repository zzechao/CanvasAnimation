package com.base.animation.item

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import com.base.animation.Animer
import com.base.animation.IAnimView
import com.base.animation.IClickIntercept
import com.base.animation.cache.AteDisplayItem
import com.base.animation.model.AnimDrawObject

/**
 * @author:zhouzechao
 * @date: 2020/12/23
 * description：单个bitmap的绘制item
 */
private const val TAG = "BitmapDisplayItem"

@AteDisplayItem(usePoolCache = true)
class BitmapDisplayItem private constructor() :
    BaseDisplayItem(Paint()) {

    private var bitmapWidth: Int = 50
    private var bitmapHeight: Int = 50
    private var mBitmapRect: Rect? = null
    private var mDisplayRect: RectF? = null
    private var displaySizeSet = false

    var mBitmap: Bitmap? = null
        set(value) {
            field = value
            bitmapWidth = value?.width ?: 50
            bitmapHeight = value?.height ?: 50
        }

    companion object {
        fun of(iAnimView: IAnimView, bitmap: Bitmap): BitmapDisplayItem {
            return BitmapDisplayItem().apply {
                Animer.log.i(TAG, "of this:$this")
                mBitmap = bitmap
                reInit()
            }
        }
    }

    override fun reInit() {
        super.reInit()
        mBitmapRect = null
        mDisplayRect = null
        displaySizeSet = false
    }

    override fun draw(
        canvas: Canvas, x: Float, y: Float, alpha: Int, scaleX: Float, scaleY: Float
    ) {
        mBitmap.takeUnless { mBitmap?.isRecycled == true }?.let {
            mPaint.alpha = alpha
            mBitmap?.let {
                if (displaySizeSet) {
                    mDisplayRect?.set(
                        x, y,
                        x + displayWidth,
                        y + displayHeight
                    )
                    mDisplayRect?.let { rect ->
                        canvas.drawBitmap(it, mBitmapRect, rect, mPaint)
                    }
                } else {
                    canvas.drawBitmap(it, x, y, mPaint)
                }
            }
        }
    }

    override fun getScalePX(scaleX: Float): Float {
        return if (displaySizeSet) {
            displayWidth * 1f / 2
        } else {
            bitmapWidth * 1f / 2
        }
    }

    override fun getScalePY(scaleY: Float): Float {
        return if (displaySizeSet) {
            displayHeight * 1f / 2
        } else {
            bitmapHeight * 1f / 2
        }
    }

    override fun getRotatePX(rotation: Float, scaleX: Float): Float {
        return if (displaySizeSet) {
            displayWidth * 1f / 2
        } else {
            bitmapWidth * 1f / 2
        }
    }

    override fun getRotatePY(rotation: Float, scaleY: Float): Float {
        return if (displaySizeSet) {
            displayHeight * 1f / 2
        } else {
            bitmapHeight * 1f / 2
        }
    }


    override fun touch(
        animId: Long,
        iClickIntercepts: MutableList<IClickIntercept>,
        animDrawObject: AnimDrawObject,
        touchPoint: MutableList<PointF>
    ) {
        var displayWidth = bitmapWidth.toFloat()
        var displayHeight = bitmapHeight.toFloat()
        if (displaySizeSet) {
            displayWidth = this.displayWidth.toFloat()
            displayHeight = this.displayHeight.toFloat()
        }
        val left = animDrawObject.point.x - displayWidth / 2 - 20
        val right = animDrawObject.point.x + displayWidth / 2 + 20
        val top = animDrawObject.point.y - displayHeight / 2 - 20
        val bottom = animDrawObject.point.y + displayHeight / 2 + 20
        touchPoint.forEach {
            if (it.x in left..right && it.y in top..bottom) {
                iClickIntercepts.forEach { iClickIntercept ->
                    iClickIntercept.intercept(animId, animDrawObject, it)
                }
            }
        }
    }

    override fun setDisplaySize(displayWidth: Int, displayHeight: Int) {
        if (displayWidth < 0 && displayHeight < 0) {
            return
        }
        this.displayWidth = displayWidth
        this.displayHeight = displayHeight
        displaySizeSet = true
        mBitmapRect = Rect(0, 0, bitmapWidth, bitmapHeight)
        mDisplayRect = RectF()
    }

    override fun recycle() {
        super.recycle()
        displaySizeSet = false
        mBitmapRect = null
        mDisplayRect = null
        bitmapWidth = 0
        bitmapHeight = 0
        mBitmap = null
    }
}