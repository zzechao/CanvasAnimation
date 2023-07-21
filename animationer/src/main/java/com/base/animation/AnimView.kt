package com.base.animation

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import com.base.animation.item.BaseDisplayItem
import com.base.animation.model.AnimPathObject
import kotlin.reflect.KClass
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

/**
 * @author:zhouzechao
 * @date: 1/8/21
 * description：普通view的canvas动画
 */
@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class AnimView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), IAnimView {
    private val helper: AnimViewHelper

    init {
        isClickable = true
        setBackgroundColor(Color.TRANSPARENT)
        helper = AnimViewHelper(context) {
            postInvalidate()
        }
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        drawAnim(canvas)
    }

    private fun drawAnim(canvas: Canvas?) {
        canvas?.drawColor(Color.TRANSPARENT) // 设置画布的背景为透明
        helper.drawAnim(canvas)
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
}