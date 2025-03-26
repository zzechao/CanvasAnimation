package com.base.animation.item

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.opengl.GLES20
import android.opengl.GLUtils
import com.base.animation.DoubleLinkedReference
import com.base.animation.OnAnimItemClick
import com.base.animation.gles.EGLRender
import com.base.animation.gles.EGLTexturePools
import com.base.animation.model.AnimDrawObject


/**
 * @author:zhouzechao
 * @date: 2020/12/23
 * description：单个bitmap的绘制item
 */
private const val TAG = "BitmapDisplayItem"

open class BitmapDisplayItem : BaseDisplayItem() {

    private val paint by lazy {
        Paint().apply {
            isAntiAlias = false
        }
    }


    private var mDisplayRect: RectF? = null
    private var displaySizeSet = false

    open var mBitmap: Bitmap? = null

    open fun setBitmap(bitmap: Bitmap) {
        mBitmap = bitmap
        if (!displaySizeSet) {
            displayWidth = bitmap.width
            displayHeight = bitmap.height
        }
    }

    override fun drawDisplayItem(canvas: Canvas, x: Float, y: Float, alpha: Int, scaleX: Float, scaleY: Float) {
        mBitmap.takeUnless { mBitmap?.isRecycled == true }?.let {
            paint.alpha = alpha
            val drawX = x - (displayWidth / scaleX / 2f)
            val drawY = y - (displayHeight / scaleY / 2f)
            mDisplayRect?.set(drawX, drawY, drawX + displayWidth, drawY + displayHeight)
            mDisplayRect?.let { rect -> canvas.drawBitmap(it, null, rect, paint) }
        }
    }

    override fun drawDisplayItem(animId: Long, render: EGLRender, x: Float, y: Float, alpha: Int, scaleX: Float, scaleY: Float, rotation: Float) {
        render.drawItem(animId, mBitmap.hashCode(), displayWidth, displayHeight, x, y, alpha, scaleX, scaleY, rotation, ::getTextureIfPresent)
    }

    private fun getTextureIfPresent(): Int {
        val texture = intArrayOf(1)
        return if (mBitmap != null && !mBitmap!!.isRecycled) {
            //生成纹理
            GLES20.glGenTextures(1, texture, 0)
            //生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0])
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST.toFloat())
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR.toFloat())
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE.toFloat())
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE.toFloat())
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0)
            texture[0]
        } else 0
    }

    override fun getScalePX(scaleX: Float): Float {
        return displayWidth / 2f
    }

    override fun getScalePY(scaleY: Float): Float {
        return displayHeight / 2f
    }

    override fun touch(animId: Long, onAnimItemClick: OnAnimItemClick, animDrawObject: AnimDrawObject, touchPoint: DoubleLinkedReference<PointF>, extra: String) {
        val displayWidth = displayWidth.toFloat()
        val displayHeight = displayHeight.toFloat()
        val left = animDrawObject.point.x - displayWidth / 2
        val right = animDrawObject.point.x + displayWidth / 2
        val top = animDrawObject.point.y - displayHeight / 2
        val bottom = animDrawObject.point.y + displayHeight / 2
        touchPoint.data?.let {
            if (it.x in left..right && it.y in top..bottom) {
                onAnimItemClick.itemClick(animId, animDrawObject, it, animDrawObject.point, extra)
                touchPoint.reset()
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
        mDisplayRect = RectF()
    }

    override fun recycle() {
        super.recycle()
        displaySizeSet = false
        mDisplayRect = null
        mBitmap = null
    }
}