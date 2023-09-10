package com.base.animation

import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.base.animation.helper.PathObjectDeal
import com.base.animation.item.BaseDisplayItem
import com.base.animation.model.AnimPathObject
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.roundToInt
import kotlin.reflect.KClass
import kotlinx.coroutines.ObsoleteCoroutinesApi

/**
 * Time:2022/4/15 12:36 下午
 * Author:
 * Description:
 */
@ObsoleteCoroutinesApi
class AnimViewHelper(context: Context, private val doFrame: () -> Unit) : IAnimView,
    CanvasHandler.CanvasFrameCallback {
    private val TAG = "AnimViewHelper"

    private val mainHandler: Handler = Handler(Looper.getMainLooper())

    private var mFramePositionCount: Int = 1
    private var isResume = AtomicBoolean(false)
    private var mFrameDuringTime = 16L // 每一帧的时间
    private var mTouchPointFList: MutableList<PointF> = mutableListOf()

    /**
     * pathObject转化
     */
    private val pathObjectDeal = PathObjectDeal(this).apply {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        val display = wm?.defaultDisplay
        var rate = display?.refreshRate ?: 60f
        if (rate < 30f) {
            rate = 60f
        }
        mFrameDuringTime = 1000L / rate.roundToInt()
        intervalDeal = mFrameDuringTime
    }

    override fun setCacheTime(cacheTime: Long) {
        pathObjectDeal.cacheTime = cacheTime
    }

    override fun resume() {
        Animer.log.i(TAG, "resume hasTask:${pathObjectDeal.hasTask()} isResume:$isResume")
        if (pathObjectDeal.hasTask()) {
            onResume()
        }
    }

    private fun onResume() {
        if (isResume.compareAndSet(false, true)) {
            Animer.log.i(TAG, "onResume")
            checkInMainThread {
                mFramePositionCount = 1
                CanvasHandler.addAnimationFrameCallback(this)
            }
        }
    }

    override fun pause() {
        if (isResume.compareAndSet(true, false)) {
            checkInMainThread {
                CanvasHandler.removeCallback(this)
                mFramePositionCount = 1
            }
        }
    }

    override fun endAnimation() {
        pause()
    }

    override fun addAnimDisplay(animPathObject: AnimPathObject) {
        pathObjectDeal.sendAnimPath(animPathObject)
        onResume()
    }

    override fun removeAnimId(animId: Long) {
        pathObjectDeal.removeAnimId(animId)
    }

    override fun hasDisplayItem(key: String, clazz: KClass<out BaseDisplayItem>): Boolean {
        return pathObjectDeal.hasDisplayItem(key, clazz)
    }

    override fun addAnimListener(iAnimListener: IAnimListener) {
        checkInMainThread {
            if (!pathObjectDeal.animListeners.contains(iAnimListener)) {
                pathObjectDeal.animListeners.add(iAnimListener)
            }
        }
    }

    override fun removeAnimListener(iAnimListener: IAnimListener?) {
        checkInMainThread {
            if (iAnimListener == null) {
                pathObjectDeal.animListeners.clear()
            } else {
                pathObjectDeal.animListeners.remove(iAnimListener)
            }
        }
    }

    override fun addClickIntercept(iClickIntercept: IClickIntercept) {
        checkInMainThread {
            if (!pathObjectDeal.clickIntercepts.contains(iClickIntercept)) {
                pathObjectDeal.clickIntercepts.add(iClickIntercept)
            }
        }
    }

    override fun removeClickIntercept(iClickIntercept: IClickIntercept?) {
        checkInMainThread {
            if (iClickIntercept == null) {
                pathObjectDeal.clickIntercepts.clear()
            } else {
                pathObjectDeal.clickIntercepts.remove(iClickIntercept)
            }
        }
    }

    override fun doCanvasFrame(frameTime: Long): Boolean {
        mFramePositionCount = if (frameTime == 0L) {
            1
        } else {
            val framePositionCount = frameTime / mFrameDuringTime
            if (framePositionCount <= 0L) {
                1
            } else {
                framePositionCount.toInt()
            }
        }
        Animer.log.i(TAG, "doCanvasFrame mFramePositionCount:$mFramePositionCount")
        doFrame.invoke()
        return true
    }

    fun touchEvent(event: MotionEvent): Boolean {
        Animer.log.i(TAG, "onTouchEvent $event")
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_POINTER_DOWN -> {
                val index = event.actionIndex
                val (xPos: Float, yPos: Float) = event.getX(index) to event.getY(index)
                mTouchPointFList.add(PointF(xPos, yPos))
            }
        }
        return false
    }

    fun drawAnim(canvas: Canvas?) {
        canvas ?: return
        if (pathObjectDeal.animDrawObjects.isNotEmpty()) {
            pathObjectDeal.animDrawObjects.map {
                it.value.draw(canvas, pathObjectDeal, mFramePositionCount, mTouchPointFList)
            }
            mTouchPointFList.clear()
        }
    }

    private fun checkInMainThread(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            block()
        } else {
            mainHandler.post { block.invoke() }
        }
    }
}