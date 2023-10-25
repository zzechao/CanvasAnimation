package com.base.animation

import android.app.Application

/**
 * @author:zhouzechao
 * description：*
 */
object AnimationEx {

    lateinit var mApplication: Application
    var mode = 1

    fun init(application: Application) {
        mApplication = application
    }

    /**
     * mode 模式1：计算动画节点预先处理，模式2：计算动画节点，根据每帧时长实时计算
     */
    fun init(application: Application, displayMaxCacheSize: Long, mode: Int = 1) {
        AnimCache.displayItemCache.displayMaxCacheSize = displayMaxCacheSize
        mApplication = application
        this.mode = mode
    }
}