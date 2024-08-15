package com.base.animation

import android.app.Application
import com.base.animation.node.IAnimNode
import com.base.animation.xml.AnimDecoder2

/**
 * @author:zhouzechao
 * description：*
 */
object AnimationEx {

    var mApplication: Application? = null
    var mode = 1

    /**
     * @param application 应用的Application
     */
    fun init(application: Application) {
        init(application, 50)
    }

    /**
     * @param application 应用的Application
     * @param displayMaxCacheSize 缓存display的大小（这里有DisplayItem的重用逻辑，根据内部声称key）
     * @param mode 模式1：计算动画节点预先处理，模式2：计算动画节点，根据每帧时长实时计算
     * @param nodeClazzs 解码器节点的注册(节点class)
     */
    fun init(application: Application, displayMaxCacheSize: Long, mode: Int = 1, vararg nodeClazzs: Class<out IAnimNode>) {
        AnimCache.displayItemCache.displayMaxCacheSize = displayMaxCacheSize
        mApplication = application
        this.mode = mode
        nodeClazzs.forEach {
            AnimDecoder2.decoder.registerNodeCreatetor(it)
        }
    }

    /**
     * 注册解码器的节点
     * @param nodeClazz 节点class
     */
    fun registerNode(nodeClazz: Class<out IAnimNode>) {
        AnimDecoder2.decoder.registerNodeCreatetor(nodeClazz)
    }
}