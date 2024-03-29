package com.base.animation

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.os.Build
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import com.base.animation.model.AnimPathObject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * @author:zhouzechao
 * @date: 2020/11/21
 * description：SurfaceView的canvas的动画
 */
@ObsoleteCoroutinesApi
class AnimSurfaceView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback, IAnimView {

    private val helper: AnimViewHelper

    private var loggingExceptionHandler = CoroutineExceptionHandler { context, throwable ->
        Animer.log.e("CoroutineException", "Coroutine exception occurred. $context", throwable)
    }


    private val animScope =
        CoroutineScope(
            SupervisorJob() + Animer.animDispatcher + loggingExceptionHandler
        )

    init {
        holder.addCallback(this)
        isFocusable = true
        keepScreenOn = true
        setZOrderOnTop(true)
        holder.setFormat(PixelFormat.TRANSPARENT)
        isFocusableInTouchMode = true
        helper = AnimViewHelper { framePositionCount, frameTime ->
            animScope.launch {
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

    override fun addClickIntercept(iClickIntercept: IClickIntercept) {
        helper.addClickIntercept(iClickIntercept)
    }

    override fun removeClickIntercept(iClickIntercept: IClickIntercept?) {
        helper.removeClickIntercept(iClickIntercept)
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

    private fun drawAnim(canvas: Canvas?, framePositionCount: Int, frameTime: Long) {
        canvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR) // 设置画布的背景为透明
        helper.drawAnim(canvas, framePositionCount, frameTime)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        pause()
    }
}