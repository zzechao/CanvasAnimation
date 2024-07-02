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
    fun onStartAnim(animId: Long, extra: String)

    /**
     * 动画执行中
     */
    fun onRunningAnim(animId: Long, extra: String)

    /**
     * 动画取消
     */
    fun onCancelAnim(animId: Long, extra: String)

    /**
     * 动画结束
     */
    fun onEndAnim(animId: Long, extra: String)
}