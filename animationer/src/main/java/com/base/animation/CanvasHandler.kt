package com.base.animation

import android.view.Choreographer

/**
 * @author:zhouzechao
 * @date: 2/8/21
 * description：仿AnimationHandler写的Choreographer处理机制
 */

private const val TAG = "CanvasHandler"

class CanvasHandler {

    private var mCanvasCallbacks: CanvasFrameCallback? = null
    private val mProvider: CanvasFrameCallbackProvider by lazy {
        MyFrameCallbackProvider()
    }

    private var lastTime = 0L

    private val mFrameCallback: Choreographer.FrameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            if (lastTime == 0L) {
                lastTime = System.nanoTime()
                doAnimationFrame(0L)
            } else {
                val curFrameTime = System.nanoTime()
                val frameDuringTime = curFrameTime - lastTime
                lastTime = curFrameTime
                doAnimationFrame(frameDuringTime / 1000000)
            }
            if (mCanvasCallbacks != null) {
                mProvider.postFrameCallback(this)
            } else {
                lastTime = 0L
            }
        }
    }

    fun setAnimationFrameCallback(
        callback: CanvasFrameCallback
    ) {
        Animer.log.i(TAG, "setAnimationFrameCallback $callback")
        if (mCanvasCallbacks == null) {
            lastTime = 0L
            mProvider.postFrameCallback(mFrameCallback)
        }
        mCanvasCallbacks = callback
    }

    fun removeCallback() {
        Animer.log.i(TAG, "removeCallback")
        mCanvasCallbacks = null
    }

    private fun doAnimationFrame(frameTime: Long) {
        Animer.log.i(TAG,"doAnimationFrame frameTime:$frameTime")
        mCanvasCallbacks?.doCanvasFrame(frameTime)
    }

    private class MyFrameCallbackProvider : CanvasFrameCallbackProvider {
        private val mChoreographer = ChoreographerKT.getChoreographer()
        override fun postFrameCallback(callback: Choreographer.FrameCallback?) {
            mChoreographer?.postFrameCallback(callback)
        }
    }

    interface CanvasFrameCallback {
        fun doCanvasFrame(frameTime: Long): Boolean
    }

    interface CanvasFrameCallbackProvider {
        fun postFrameCallback(callback: Choreographer.FrameCallback?)
    }
}