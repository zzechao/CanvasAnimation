package com.base.animation

import android.graphics.Canvas
import android.graphics.PointF
import android.os.Looper
import android.view.MotionEvent
import com.base.animation.helper.PathObjectDeal
import com.base.animation.helper.PathObjectDeal2
import com.base.animation.model.AnimDrawObject
import com.base.animation.model.AnimPathObject
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Time:2022/4/15 12:36 下午
 * Author: zhouzechao
 * Description:
 */
typealias DoFrameFps = (framePositionCount: Int, frameTime: Long) -> Unit

class AnimViewHelper(var isSurfaceView: Boolean = false, private val doFrame: DoFrameFps) : IAnimView, CanvasHandler.CanvasFrameCallback {
    private val TAG = "AnimViewHelper"

    private val canvasHandler by lazy {
        CanvasHandler()
    }


    private var isResume = AtomicBoolean(false)
    private var mTouchPointF: PointF? = null

    /**
     * pathObject转化
     */
    private val pathObjectDeal by lazy {
        if (AnimationEx.mode == 1) {
            PathObjectDeal()
        } else {
            PathObjectDeal2()
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
            if (isSurfaceView) {
                ChoreographerKT.animViewHandler.post {
                    canvasHandler.setAnimationFrameCallback(this)
                }
            } else {
                ChoreographerKT.mainHandler.post {
                    canvasHandler.setAnimationFrameCallback(this)
                }
            }
        }
    }

    override fun pause() {
        if (isResume.compareAndSet(true, false)) {
            canvasHandler.removeCallback()
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
        pathObjectDeal.animListeners.add(iAnimListener)
    }

    override fun removeAnimListener(iAnimListener: IAnimListener?) {
        if (iAnimListener == null) {
            pathObjectDeal.animListeners.clear()
        } else {
            pathObjectDeal.animListeners.remove(iAnimListener)
        }
    }

    override fun setOnItemClick(onItemClick: OnAnimItemClick?) {
        pathObjectDeal.onItemListener = null
        onItemClick?.let {
            pathObjectDeal.onItemListener = object : OnAnimItemClick {
                override fun itemClick(animId: Long, animDrawObject: AnimDrawObject, touchPointF: PointF, itemCenterPointF: PointF, extra: String) {
                    checkInMainThread {
                        it.itemClick(animId, animDrawObject, touchPointF, itemCenterPointF, extra)
                    }
                }
            }
        }
    }

    override fun doCanvasFrame(frameTime: Long): Boolean {
        val framePositionCount = if (frameTime == 0L) {
            1
        } else {
            val framePositionCount = frameTime / fpsTime
            if (framePositionCount <= 1) {
                1
            } else {
                framePositionCount.toInt()
            }
        }
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
                mTouchPointF = PointF(xPos, yPos)
            }
        }
        return false
    }

    fun drawAnim(canvas: Canvas?, framePositionCount: Int, frameTime: Long): Boolean {
        canvas ?: return false
        val doubleLinkedReference = mTouchPointF?.let {
            DoubleLinkedReference(it)
        }
        pathObjectDeal.animDrawObjects.map {
            it.value.draw(canvas, pathObjectDeal, framePositionCount, frameTime, doubleLinkedReference)
        }
        mTouchPointF = null
        return pathObjectDeal.animDrawObjects.isNotEmpty()
    }

    private fun checkInMainThread(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            block()
        } else {
            ChoreographerKT.mainHandler.post { block.invoke() }
        }
    }
}