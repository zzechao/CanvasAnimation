package com.base.animation.item

import android.content.Context
import android.graphics.*
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import androidx.core.graphics.withSave
import com.base.animation.Animer
import com.base.animation.DoubleLinkedReference
import com.base.animation.OnAnimItemClick
import com.base.animation.gles.EGLAnimTexture
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
        val widthSpec = View.MeasureSpec.makeMeasureSpec(displayWidth, View.MeasureSpec.UNSPECIFIED)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(displayHeight, View.MeasureSpec.UNSPECIFIED)
        view.measure(widthSpec, heightSpec)
        displayWidth = view.measuredWidth
        displayHeight = view.measuredHeight
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        Animer.log.d(tag, "LayoutDisplayItem: $layout ${view.measuredWidth} ${view.measuredHeight}")
    }

    override fun drawDisplayItem(
        canvas: Canvas, x: Float, y: Float, alpha: Int, scaleX: Float, scaleY: Float
    ) {
        val drawY = y - (displayHeight / scaleY / 2f)
        canvas.translate(x, drawY)
        view.alpha = alpha.toFloat()
        view.draw(canvas)
    }

    override fun drawDisplayItem(animId: Long, render: EGLRender, x: Float, y: Float, alpha: Int, scaleX: Float, scaleY: Float, rotation: Float) {
        render.drawItem(animId, layout.hashCode(), displayWidth, displayHeight, x, y, alpha, scaleX, scaleY, rotation, ::getTextureIfPresent)
    }

    private fun getTextureIfPresent(): EGLAnimTexture {
        val externalTextureId = IntArray(1)
        GLES20.glGenTextures(1, externalTextureId, 0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, externalTextureId[0])
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

        // 创建SurfaceTexture和Surface
        val textureId = externalTextureId[0]
        val eglAnimTexture = EGLAnimTexture(textureId, EGLAnimTexture.TextureType.LAYOUT)
        val surfaceTexture = SurfaceTexture(textureId)
        surfaceTexture.setDefaultBufferSize(displayWidth, displayHeight)
        val surface = Surface(surfaceTexture)
        eglAnimTexture.surfaceTexture = surfaceTexture
        eglAnimTexture.surface = surface
        val canvas = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            surface.lockHardwareCanvas()
        } else {
            surface.lockCanvas(null)
        }
        canvas?.let {
            view.draw(it)
            surface.unlockCanvasAndPost(canvas)
        }
        Animer.log.d(tag, "getTextureIfPresent: $textureId")
        return eglAnimTexture
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