package com.base.canvasanimation

import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Shader
import android.util.AttributeSet
import com.base.animation.CanvasHandler


/**
 * @author:zhouz
 * @date: 2023/10/19 18:44
 * description：渐变动画TextView
 */
class GradientColorAnimTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : androidx.appcompat.widget.AppCompatTextView(context, attrs),
    CanvasHandler.CanvasFrameCallback {

    private var mTranslate = 0f
    private var mTextWidth: Float = 0f
    private var mLinearGradient: LinearGradient? = null
    private var curDurTime = 0f
    private var mAnimating: Boolean = false
    private val mGradientMatrix: Matrix by lazy {
        Matrix()
    }

    var delta = 10f
    var size = 60f
    val fpsTime = 30f
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (mTextWidth == 0f) {
            mTextWidth = paint.measureText(text.toString())
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (mAnimating) {
            CanvasHandler.addAnimationFrameCallback(this)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mAnimating) {
            CanvasHandler.removeCallback(this)
        }
    }

    fun start() {
        if (!mAnimating) {
            mAnimating = true
            CanvasHandler.addAnimationFrameCallback(this)
            mLinearGradient = LinearGradient(
                -size, 0f, 0f, 0f, intArrayOf(
                    paint.color, 0xFFFAE510.toInt(), paint.color
                ), floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP
            ) //边缘融合
            paint.shader = mLinearGradient
        }
    }

    fun stop() {
        if (mAnimating) {
            mAnimating = false
            CanvasHandler.removeCallback(this)
            paint.shader = null
            curDurTime = 0f
            mTranslate = 0f
            postInvalidate()
        }
    }

    override fun doCanvasFrame(frameTime: Long): Boolean {
        curDurTime += frameTime
        if (curDurTime > fpsTime) {
            curDurTime = 0f
            mTranslate += delta
            if (mTranslate > mTextWidth + delta) {
                mTranslate = 0f
            }
            if (mAnimating) {
                mGradientMatrix.setTranslate(mTranslate, 0f)
                mGradientMatrix.postRotate(30f)
                mLinearGradient?.setLocalMatrix(mGradientMatrix)
            }
            postInvalidate()
        }
        return true
    }
}


/**
 * 启动渐变动画
 */
fun GradientColorAnimTextView.starter(builder: (GradientColorAnimTextView.() -> Unit)? = null) {
    builder?.invoke(this)
    start()
}