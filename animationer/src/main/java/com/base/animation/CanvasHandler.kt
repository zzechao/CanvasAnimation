package com.base.animation

import android.content.Context
import android.view.Choreographer
import android.view.WindowManager

/**
 * @author:zhouzechao
 * @date: 2/8/21
 * description：仿AnimationHandler写的Choreographer处理机制
 */
object CanvasHandler {

    private val mCanvasCallbacks = ArrayList<CanvasFrameCallback>()
    private var mProvider: CanvasFrameCallbackProvider? = null

    val fpsTime: Float by lazy {
        val wm = AnimationEx.mApplication?.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        val display = wm?.defaultDisplay
        val rate = display?.refreshRate ?: 60f
        1000 * 1f / rate
    }


    var lastTime = 0L

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
            if (mCanvasCallbacks.isNotEmpty()) {
                getProvider()?.postFrameCallback(this)
            } else {
                lastTime = 0L
            }
        }
    }

    fun addAnimationFrameCallback(
        callback: CanvasFrameCallback
    ) {
        if (mCanvasCallbacks.size == 0) {
            lastTime = 0L
            getProvider()?.postFrameCallback(mFrameCallback)
        }
        if (!mCanvasCallbacks.contains(callback)) {
            mCanvasCallbacks.add(callback)
        }
    }

    fun removeCallback(callback: CanvasFrameCallback?) {
        mCanvasCallbacks.remove(callback)
    }

    fun getProvider(): CanvasFrameCallbackProvider? {
        if (mProvider == null) {
            mProvider = MyFrameCallbackProvider()
        }
        return mProvider
    }

    private fun doAnimationFrame(frameTime: Long) {
        mCanvasCallbacks.forEach {
            it.doCanvasFrame(frameTime)
        }
    }

    private class MyFrameCallbackProvider : CanvasFrameCallbackProvider {
        private val mChoreographer = Choreographer.getInstance()
        override fun postFrameCallback(callback: Choreographer.FrameCallback?) {
            mChoreographer.postFrameCallback(callback)
        }
    }

    interface CanvasFrameCallback {
        fun doCanvasFrame(frameTime: Long): Boolean
    }

    interface CanvasFrameCallbackProvider {
        fun postFrameCallback(callback: Choreographer.FrameCallback?)
    }
}