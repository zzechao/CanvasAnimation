package com.base.animation

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.view.Choreographer


/**
 * @author:zhouz
 * @date: 2024/7/1 17:55
 * description：根据是否SurfaceView 区分 post frame的 looper
 */
object ChoreographerKT {
    internal val mainHandler = Handler(Looper.getMainLooper())
    private val mainChoreographer by lazy { Choreographer.getInstance() }

    internal val animViewHandler: Handler by lazy {
        val handlerThread = HandlerThread("AnimPlayer_Handler")
        handlerThread.start()
        Handler(handlerThread.looper)
    }
    private val surfaceViewChoreographer by lazy { Choreographer.getInstance() }

    /**
     * 根据不同looper 构造
     */
    fun getChoreographer(): Choreographer? {
        return if (Looper.myLooper() == Looper.getMainLooper()) {
            mainChoreographer
        } else if (Looper.myLooper() == animViewHandler.looper) {
            surfaceViewChoreographer
        } else {
            null
        }
    }
}