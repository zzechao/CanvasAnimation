package com.base.animation.item

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import androidx.core.graphics.withSave
import com.base.animation.IClickIntercept
import com.base.animation.cache.AteDisplayItem
import com.base.animation.model.AnimDrawObject
import com.base.animation.xml.AnimDecoder2

/**
 * @author:zhouzechao
 * @date: 2020/12/23
 * description：单个bitmap的绘制item
 */
private const val TAG = "BitmapDisplayItem"

@AteDisplayItem(usePoolCache = true)
open class BitmapDisplayItem : BaseDisplayItem() {


    private val paint by lazy {
        Paint().apply {
            isAntiAlias = false
            isFilterBitmap = true
        }
    }

    private var bitmapWidth: Int = 50
    private var bitmapHeight: Int = 50
    private var mBitmapRect: Rect? = null
    private var mDisplayRect: RectF? = null
    private var displaySizeSet = false

    open var mBitmap: Bitmap? = null
        set(value) {
            field = value
            bitmapWidth = value?.width ?: 50
            bitmapHeight = value?.height ?: 50
            Log.i(TAG, "$bitmapWidth - $bitmapHeight")
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
        canvas.withSave {
            if (displaySizeSet) {
                val drawX = x - (displayWidth / scaleX / 2f)
                val drawY = y - (displayHeight / scaleY / 2f)

                if (rotation != 0f) {
                    canvas.rotate(
                        rotation, drawX + getRotatePX(rotation, scaleX),
                        drawY + getRotatePY(rotation, scaleY)
                    )
                }
            } else {
                val drawX = x - (bitmapWidth / 2 / scaleX)
                val drawY = y - (bitmapHeight / 2 / scaleY)
                if (rotation != 0f) {
                    canvas.rotate(
                        rotation, drawX + getRotatePX(rotation, scaleX),
                        drawY + getRotatePY(rotation, scaleY)
                    )
                }
            }


            if (scaleX != 1f || scaleY != 1f) {
                canvas.scale(scaleX, scaleY, x + getScalePX(scaleX), y + getScalePY(scaleY))
            }

            draw(canvas, x, y, alpha, scaleX, scaleY)
        }
    }

    override fun draw(
        canvas: Canvas, x: Float, y: Float, alpha: Int, scaleX: Float, scaleY: Float
    ) {
        mBitmap.takeUnless { mBitmap?.isRecycled == true }?.let {
            paint.alpha = alpha
            mBitmap?.let {
                if (displaySizeSet) {
                    val drawX = x - (displayWidth / scaleX / 2f)
                    val drawY = y - (displayHeight / scaleY / 2f)

                    mDisplayRect?.set(
                        drawX, drawY,
                        drawX + displayWidth,
                        drawY + displayHeight
                    )
                    mDisplayRect?.let { rect ->
                        canvas.drawBitmap(it, null, rect, paint)
                    }
                } else {
                    val drawX = x - (bitmapWidth / 2 / scaleX)
                    val drawY = y - (bitmapHeight / 2 / scaleY)
                    canvas.drawBitmap(it, drawX, drawY, paint)
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
            displayWidth / scaleX / 2f
        } else {
            bitmapWidth / scaleX / 2f
        }
    }

    override fun getRotatePY(rotation: Float, scaleY: Float): Float {
        return if (displaySizeSet) {
            displayHeight / scaleY / 2f
        } else {
            bitmapHeight / scaleY / 2f
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