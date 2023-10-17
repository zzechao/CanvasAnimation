package com.base.animation

import android.graphics.Canvas
import android.graphics.PointF
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import com.base.animation.helper.PathObjectDeal
import com.base.animation.helper.PathObjectDeal2
import com.base.animation.model.AnimPathObject
import kotlinx.coroutines.ObsoleteCoroutinesApi
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Time:2022/4/15 12:36 下午
 * Author:
 * Description:
 */
typealias DoFrameFps = (framePositionCount: Int, frameTime: Long) -> Unit

@ObsoleteCoroutinesApi
class AnimViewHelper(private val doFrame: DoFrameFps) : IAnimView,
    CanvasHandler.CanvasFrameCallback {
    private val TAG = "AnimViewHelper"

    private val mainHandler: Handler = Handler(Looper.getMainLooper())

    private var isResume = AtomicBoolean(false)
    private var mTouchPointFList: MutableList<PointF> = mutableListOf()

    /**
     * pathObject转化
     */
    private val pathObjectDeal by lazy {
        if (AnimationEx.mode == 1) {
            PathObjectDeal(this)
        } else {
            PathObjectDeal2(this)
        }
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
                CanvasHandler.addAnimationFrameCallback(this)
            }
        }
    }

    override fun pause() {
        if (isResume.compareAndSet(true, false)) {
            checkInMainThread {
                CanvasHandler.removeCallback(this)
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
        val framePositionCount = if (frameTime == 0L) {
            1
        } else {
            val framePositionCount = frameTime / CanvasHandler.fpsTime
            if (framePositionCount <= 1) {
                1
            } else {
                framePositionCount.toInt()
            }
        }
        Animer.log.i(
            TAG,
            "doCanvasFrame mFramePositionCount:$framePositionCount frameTime:$frameTime"
        )
        doFrame.invoke(framePositionCount, frameTime)
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

    fun drawAnim(canvas: Canvas?, framePositionCount: Int, frameTime: Long) {
        canvas ?: return
        pathObjectDeal.animDrawObjects.map {
            it.value.draw(canvas, pathObjectDeal, framePositionCount, frameTime, mTouchPointFList)
        }
        mTouchPointFList.clear()
    }

    private fun checkInMainThread(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            block()
        } else {
            mainHandler.post { block.invoke() }
        }
    }
}