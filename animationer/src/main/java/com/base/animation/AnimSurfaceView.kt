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
import com.base.animation.item.BaseDisplayItem
import com.base.animation.model.AnimPathObject
import kotlin.reflect.KClass
import kotlinx.coroutines.ObsoleteCoroutinesApi

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

    init {
        holder.addCallback(this)
        isFocusable = true
        keepScreenOn = true
        setZOrderOnTop(true)
        holder.setFormat(PixelFormat.TRANSLUCENT)
        holder.setType(View.LAYER_TYPE_HARDWARE)
        isFocusableInTouchMode = true
        helper = AnimViewHelper(context) {
            val canvas = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                holder.lockHardwareCanvas()
            } else {
                holder.lockCanvas()
            }
            drawAnim(canvas)
            holder.unlockCanvasAndPost(canvas)
        }
    }

    override fun setCacheTime(cacheTime: Long) {
        helper.setCacheTime(cacheTime)
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

    override fun hasDisplayItem(key: String, clazz: KClass<out BaseDisplayItem>): Boolean {
        return helper.hasDisplayItem(key, clazz)
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

    override fun <T : BaseDisplayItem> obtain(clazz: KClass<out BaseDisplayItem>): T? {
        return helper.obtain(clazz)
    }

    private fun drawAnim(canvas: Canvas?) {
        canvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR) // 设置画布的背景为透明
        helper.drawAnim(canvas)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        pause()
    }
}