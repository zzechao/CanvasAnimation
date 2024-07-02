package com.base.animation

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import com.base.animation.model.AnimPathObject

/**
 * @author:zhouzechao
 * @date: 2020/11/21
 * description：SurfaceView的canvas的动画
 */
open class AnimSurfaceView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback, IAnimView {

    private var isSurfaceRelease: Boolean = true
    private val helper: AnimViewHelper

    init {
        holder.addCallback(this)
        isFocusable = true
        keepScreenOn = true
        holder.setFormat(PixelFormat.TRANSPARENT)
        //isFocusableInTouchMode = true
        helper = AnimViewHelper(isSurfaceView = true) { framePositionCount, frameTime ->
            tryCatch {
                if (isSurfaceRelease) return@tryCatch
                val canvas = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    holder.lockHardwareCanvas()
                } else {
                    holder.lockCanvas()
                }
                drawAnim(canvas, framePositionCount, frameTime)
                holder.unlockCanvasAndPost(canvas)
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setZOrderOnTop(true)
    }

    override fun resume() {
        helper.resume()
    }

    override fun pause() {
        helper.pause()
    }

    override fun removeAnimId(animId: Long) {
        helper.removeAnimId(animId)
    }

    /**
     * 结束动画
     */
    override fun endAnimation() {
        helper.endAnimation()
    }

    /**
     * 添加动画播放
     */
    override fun addAnimDisplay(animPathObject: AnimPathObject) {
        helper.addAnimDisplay(animPathObject)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        endAnimation()
        holder.removeCallback(this)
        holder.surface.release()
    }

    override fun addAnimListener(iAnimListener: IAnimListener) {
        helper.addAnimListener(iAnimListener)
    }

    override fun removeAnimListener(iAnimListener: IAnimListener?) {
        helper.removeAnimListener(iAnimListener)
    }

    override fun setOnItemClick(onItemClick: OnAnimItemClick?) {
        helper.setOnItemClick(onItemClick)
    }

    override fun getView(): View {
        return this
    }

    override fun getViewByAnimName(name: String): View? {
        return tryCatch {
            val context = AnimationEx.mApplication ?: return@tryCatch null
            val id =
                context.resources.getIdentifier(
                    name,
                    "id",
                    context.packageName
                )
            this.findFragmentOfGivenView()?.let {
                it.view?.findViewById<View>(id)
            } ?: this.getFragmentActivity()?.findViewById(id)
        }
    }

    /**
     * open drawAnim方法
     */
    open fun drawAnim(canvas: Canvas?, framePositionCount: Int, frameTime: Long) {
        canvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR) // 设置画布的背景为透明
        helper.drawAnim(canvas, framePositionCount, frameTime)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        isSurfaceRelease = false
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        isSurfaceRelease = true
        pause()
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            helper.touchEvent(event)
        }
        return super.dispatchTouchEvent(event)
    }
}