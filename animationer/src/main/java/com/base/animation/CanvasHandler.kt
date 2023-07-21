package com.base.animation

import android.view.Choreographer
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * @author:zhouzechao
 * @date: 2/8/21
 * description：仿AnimationHandler写的Choreographer处理机制
 */
object CanvasHandler {

    private val mCanvasCallbacks = ArrayList<CanvasFrameCallback>()
    private var mProvider: CanvasFrameCallbackProvider? = null

    var lastTime = 0L


    private var loggingExceptionHandler = CoroutineExceptionHandler { context, throwable ->
        Animer.log.e("CoroutineException", "Coroutine exception occurred. $context", throwable)
    }


    private val animScope =
        CoroutineScope(
            SupervisorJob() + Animer.animDispatcher + loggingExceptionHandler
        )

    private val mFrameCallback: Choreographer.FrameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            if (lastTime == 0L) {
                lastTime = System.nanoTime() / 1000000
                doAnimationFrame(0L)
            } else {
                val curFrameTime = System.nanoTime() / 1000000
                val frameDuringTime = curFrameTime - lastTime
                lastTime = curFrameTime
                doAnimationFrame(frameDuringTime)
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
        animScope.launch {
            mCanvasCallbacks.forEach {
                it.doCanvasFrame(frameTime)
            }
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