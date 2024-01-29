package com.base.animation

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.base.animation.model.AnimPathObject
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
        var frameCount = 0
        var curFrameTime = 0L
        helper = AnimViewHelper { framePositionCount, frameTime ->
            frameCount = framePositionCount
            curFrameTime = frameTime
            background.invalidateSelf()
        }
        background = AnimViewDrawable {
            helper.drawAnim(it, frameCount, curFrameTime)
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
}