package com.base.animation

import android.app.Application

object AnimationEx {

    lateinit var mApplication: Application

    fun init(application: Application) {
        mApplication = application
    }

    fun init(application: Application, displayMaxCacheSize: Long) {
        AnimCache.displayItemCache.displayMaxCacheSize = displayMaxCacheSize
    }
}