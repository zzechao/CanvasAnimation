package com.base.animation

/**
 * @author:zhouzechao
 * @date: 1/25/21
 * description：动画监听器
 */
interface IAnimListener {

    /**
     * 动画开始
     */
    fun startAnim(animId: Long)

    /**
     * 动画执行中
     */
    fun runningAnim(animId: Long)

    /**
     * 动画结束
     */
    fun endAnim(animId: Long)
}